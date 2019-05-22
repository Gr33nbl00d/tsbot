package de.greenblood.tsbot.plugins.autochannel;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.greenblood.tsbot.caches.ClientInfoRetriever;
import de.greenblood.tsbot.common.DefaultTsBotPlugin;
import de.greenblood.tsbot.common.MessageFormatingBuilder;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;

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
public class AutoChannelPlugin extends DefaultTsBotPlugin {

  private TsApiUtils tsApiUtils = new TsApiUtils();
  @Autowired
  private AutoChannelPluginConfig autoChannelPluginConfig;
  private Map<String, ChannelBase> channelNameToParentChannelMap = new HashMap<>();
  private Map<String, String> commandChannelMap = new HashMap<>();
  private Map<Integer, AutoChannelConfig> channelIdToConfigMap = new HashMap<>();

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

          createChannel(context, parentChannel.getId(), e.getInvokerId(), playerCountStr);
          //context.getApi().editChannel(channel, ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
          return;
        }
      }
    }
  }

  private void createChannel(Ts3BotContext context, int parentChannelId, int clientId, String playerCountStr) {
    Map<ChannelProperty, String> channelProperties = new HashMap<>();
    AutoChannelConfig autoChannelConfig = this.channelIdToConfigMap.get(parentChannelId);
    ClientInfo client = ClientInfoRetriever.getInstance().retrieve(context, clientId, true);

    channelProperties.put(ChannelProperty.CPID, "" + parentChannelId);
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
    }
  }

  @Override
  public void init(Ts3BotContext context) {
    List<AutoChannelConfig> autoChannelList = this.autoChannelPluginConfig.getAutoChannelList();
    for (AutoChannelConfig autoChannelConfig : autoChannelList) {
      String channelName = autoChannelConfig.getChannelSearchString();
      ChannelBase channel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), channelName);
      this.channelNameToParentChannelMap.put(channelName, channel);
      this.commandChannelMap.put(autoChannelConfig.getCommand(), channelName);
      this.channelIdToConfigMap.put(channel.getId(), autoChannelConfig);
    }
  }

  @Override
  public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {
    for (ChannelBase channel : this.channelNameToParentChannelMap.values()) {
      if (e.getTargetChannelId() == channel.getId()) {
        AutoChannelConfig config = this.channelIdToConfigMap.get(channel.getId());
        String commandName = config.getCommand();

        if (config.isAutoCreateOnJoin()) {
          createChannel(context, channel.getId(), e.getClientId(), null);
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

}
