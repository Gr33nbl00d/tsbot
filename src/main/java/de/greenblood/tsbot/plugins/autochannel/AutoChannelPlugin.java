package de.greenblood.tsbot.plugins.autochannel;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import de.greenblood.tsbot.common.MessageFormattingUtil;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;
import de.greenblood.tsbot.TsBotPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class AutoChannelPlugin implements TsBotPlugin
{
    private TsApiUtils tsApiUtils = new TsApiUtils();
    @Autowired
    private AutoChannelPluginConfig autoChannelPluginConfig;
    private Map<String, Channel> channelMap = new HashMap<>();
    private Map<String, String> commandChannelMap = new HashMap<>();
    private Map<Integer, AutoChannelConfig> channelIdToConfigMap = new HashMap<>();
    private final MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e)
    {
    }

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e)
    {
        if (tsApiUtils.isPrivateMessageForBot(e, context.getSessionInfo()))
        {
            for (Map.Entry<String, String> entry : this.commandChannelMap.entrySet())
            {
                String regex = "\\" + entry.getKey() + "\\s*(\\d{0,3})";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(e.getMessage());
                if (matcher.matches())
                {
                    Map<ChannelProperty, String> channelProperties = new HashMap<>();
                    String channelName = this.commandChannelMap.get(entry.getKey());
                    Channel parentChannel = this.channelMap.get(channelName);
                    channelProperties.put(ChannelProperty.CPID, "" + parentChannel.getId());

                    String playerCountStr = matcher.group(1);
                    if ("".equals(playerCountStr) == false)
                    {
                        channelProperties.put(ChannelProperty.CHANNEL_MAXCLIENTS, playerCountStr);
                        channelProperties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
                    }
                    else
                    {
                        channelProperties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "1");
                    }

                    channelProperties.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
                    channelProperties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "0");
                    channelProperties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "0");
                    AutoChannelConfig autoChannelConfig = this.channelIdToConfigMap.get(parentChannel.getId());
                    int channel = context.getApi().createChannel(messageFormattingUtil.formatNewChannel(autoChannelConfig.getNewChannelName(), e), channelProperties);
                    context.getApi().moveClient(e.getInvokerId(), channel);
                    context.getApi().editChannel(channel, ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "0");
                    //context.getApi().editChannel(channel, ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
                    return;
                }
            }
        }
    }

    @Override
    public void init(Ts3BotContext context)
    {
        List<AutoChannelConfig> autoChannelList = this.autoChannelPluginConfig.getAutoChannelList();
        for (AutoChannelConfig autoChannelConfig : autoChannelList)
        {
            String channelName = autoChannelConfig.getChannelName();
            Channel channel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), channelName, true);
            this.channelMap.put(channelName, channel);
            this.commandChannelMap.put(autoChannelConfig.getCommand(), channelName);
            this.channelIdToConfigMap.put(channel.getId(), autoChannelConfig);
        }
    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e)
    {
        for (Channel channel : this.channelMap.values())
        {
            if (e.getTargetChannelId() == channel.getId())
            {
                AutoChannelConfig config = this.channelIdToConfigMap.get(channel.getId());
                String commandName = config.getCommand();
                for (String message : config.getAutoChannelHelloMessages())
                {
                    context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), messageFormattingUtil.formatCommand(message, commandName));
                }
            }
        }
    }

}
