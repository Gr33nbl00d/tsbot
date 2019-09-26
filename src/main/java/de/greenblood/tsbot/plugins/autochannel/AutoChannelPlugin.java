package de.greenblood.tsbot.plugins.autochannel;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Permission;
import de.greenblood.tsbot.caches.ClientInfoRetriever;
import de.greenblood.tsbot.common.*;
import de.greenblood.tsbot.common.UpdateablePluginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class AutoChannelPlugin extends UpdatableTsBotPlugin<AutoChannelPluginConfig> {

    private TsApiUtils tsApiUtils = new TsApiUtils();
    @Autowired
    private AutoChannelPluginConfig autoChannelPluginConfig;
    private Map<String, ChannelBase> channelNameToParentChannelMap;
    private Map<String, String> commandChannelMap;
    private Map<Integer, AutoChannelConfig> channelIdToConfigMap;
    private boolean init;

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e) {
        if (tsApiUtils.isPrivateMessageForBot(e, context.getSessionInfo())) {
            for (Map.Entry<String, String> entry : this.commandChannelMap.entrySet()) {
                String regex = "\\" + entry.getKey() + "\\s*(\\d{0,3})";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(e.getMessage());
                if (matcher.matches()) {
                    String channelName = this.commandChannelMap.get(entry.getKey());
                    ChannelBase parentChannel = this.channelNameToParentChannelMap.get(channelName);

                    String playerCountStr = matcher.group(1);

                    createChannel(context, parentChannel, e.getInvokerId(), playerCountStr);
                    //context.getApi().editChannel(channel, ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
                    return;
                }
            }
        }
    }

    private void createChannel(Ts3BotContext context, ChannelBase parentChannel, int clientId, String playerCountStr) {
        Map<ChannelProperty, String> channelProperties = new HashMap<>();
        AutoChannelConfig autoChannelConfig = this.channelIdToConfigMap.get(parentChannel.getId());
        ClientInfo client = ClientInfoRetriever.getInstance().retrieve(context, clientId, true);
        channelProperties.put(ChannelProperty.CPID, "" + parentChannel.getId());
        copyPropertiesFromParentChannel(parentChannel, channelProperties);
        String newChannelName;
        if (playerCountStr != null && "".equals(playerCountStr) == false) {
            channelProperties.put(ChannelProperty.CHANNEL_MAXCLIENTS, playerCountStr);
            channelProperties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
        } else {
            channelProperties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "1");
        }
        newChannelName = new MessageFormatingBuilder()
                .addClient(client)
                .build(autoChannelConfig.getNewChannelName());

        ChannelBase existingChannel = context.getApi().getChannelByNameExact(newChannelName, false);
        if (existingChannel != null) {
            context.getAsyncApi().editChannel(existingChannel.getId(), channelProperties);
        } else {
            channelProperties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "0");
            channelProperties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "0");
            channelProperties.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
            int channel = context.getApi().createChannel(newChannelName, channelProperties);
            context.getApi().moveClient(clientId, channel);
            context.getApi().editChannel(channel, ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "0");

            applyPermissionsFromParentChannel(context, parentChannel, channel);


        }
    }

    private void applyPermissionsFromParentChannel(Ts3BotContext context, ChannelBase parentChannel, int channel) {
        List<Permission> channelPermissions = context.getApi().getChannelPermissions(parentChannel.getId());

        for (Permission channelPermission : channelPermissions) {
            context.getApi().addChannelPermission(channel, channelPermission.getName(), channelPermission.getValue());
        }
    }

    private void copyPropertiesFromParentChannel(ChannelBase parentChannel, Map<ChannelProperty, String> channelProperties) {
        List<ChannelProperty> propertiesToCopy = new ArrayList<>();
        propertiesToCopy.add(ChannelProperty.CHANNEL_NEEDED_TALK_POWER);

        for (ChannelProperty channelProperty : propertiesToCopy) {
            channelProperties.put(channelProperty, parentChannel.get(channelProperty));
        }
    }

    @Override
    public void init(Ts3BotContext context) {
        init = false;
        channelNameToParentChannelMap = new HashMap<>();
        commandChannelMap = new HashMap<>();
        channelIdToConfigMap = new HashMap<>();
        List<AutoChannelConfig> autoChannelList = this.autoChannelPluginConfig.getAutoChannelList();
        for (AutoChannelConfig autoChannelConfig : autoChannelList) {
            String channelName = autoChannelConfig.getChannelSearchString();
            ChannelBase channel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), channelName);
            this.channelNameToParentChannelMap.put(channelName, channel);
            this.commandChannelMap.put(autoChannelConfig.getCommand(), channelName);
            this.channelIdToConfigMap.put(channel.getId(), autoChannelConfig);
        }
        init = true;
    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {
        if (init == false)
            return;
        for (ChannelBase channel : this.channelNameToParentChannelMap.values()) {
            if (e.getTargetChannelId() == channel.getId()) {
                AutoChannelConfig config = this.channelIdToConfigMap.get(channel.getId());
                String commandName = config.getCommand();

                if (config.isAutoCreateOnJoin()) {
                    createChannel(context, channel, e.getClientId(), null);
                    for (String message : config.getAutoChannelCreatedMessages()) {
                        message = new MessageFormatingBuilder().addChatCommand(commandName).build(message);
                        context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), message);
                    }
                } else {
                    for (String message : config.getAutoChannelHelloMessages()) {
                        message = new MessageFormatingBuilder()
                                .addChatCommand(commandName)
                                .build(message);

                        context.getAsyncApi()
                                .sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), message);
                    }
                }
                return;
            }
        }
    }

    @Override
    public Class<AutoChannelPluginConfig> getConfigClass() {
        return AutoChannelPluginConfig.class;
    }

    @Override
    public void reloadPlugin(Ts3BotContext context) {
        init(context);
    }

    @Override
    public String getReadWriteAuthorityName() {
        return "autochannel_maintainer";
    }

    @Override
    public UpdateablePluginConfig<AutoChannelPluginConfig> getConfig() {
        return this.autoChannelPluginConfig;
    }
}
