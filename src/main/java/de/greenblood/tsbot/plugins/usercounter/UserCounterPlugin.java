package de.greenblood.tsbot.plugins.usercounter;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.greenblood.tsbot.caches.ClientsOnlineRetriever;
import de.greenblood.tsbot.common.DefaultTsBotPlugin;
import de.greenblood.tsbot.common.MessageFormatingBuilder;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;
import de.greenblood.tsbot.plugins.support.IncludedInServerGroupFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class UserCounterPlugin extends DefaultTsBotPlugin {

  private final TsApiUtils tsApiUtils = new TsApiUtils();

  @Autowired
  private UserCounterPluginConfig userCounterPluginConfig;

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    updateCounters(context);
  }

  @Override
  public void onClientLeave(Ts3BotContext context, ClientLeaveEvent e) {
    updateCounters(context);
  }

  @Override
  public void init(Ts3BotContext context) {
    List<UserCounterPluginConfig.UserCounterConfig> userCounterList = userCounterPluginConfig.getUserCounterList();
    updateCounters(context);
  }

  private void updateCounters(Ts3BotContext context) {
    List<UserCounterPluginConfig.UserCounterConfig> userCounterList = userCounterPluginConfig.getUserCounterList();
    for (UserCounterPluginConfig.UserCounterConfig userCounterConfig : userCounterList) {
      ChannelBase channel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), userCounterConfig.getChannelSearchString());

      Predicate<Client> filter = new ServerQueryFilter().negate();
      if (userCounterConfig.getServerGroupsToCount() != null) {
        filter = filter.and(new IncludedInServerGroupFilter(userCounterConfig.getServerGroupsToCount()));
      }
      if (userCounterConfig.getServerGroupsToIgnore() != null) {
        filter = filter.and(new IncludedInServerGroupFilter(userCounterConfig.getServerGroupsToIgnore()).negate());
      }
      int clients = ClientsOnlineRetriever.getInstance().getFilteredClients(context, 0, filter).size();
      String oldChannelName = channel.getName();
      String newChannelName = new MessageFormatingBuilder()
          .addUserCount(clients)
          .build(userCounterConfig.getChannelNameTemplate());

      if (oldChannelName.equals(newChannelName) == false) {
        context.getApi().editChannel(channel.getId(), ChannelProperty.CHANNEL_NAME, newChannelName);
      }
      UserCounterPluginConfig.UserCounterConfig.UserCounterOnlineRecordConfig
          onlineRecordConfig = userCounterConfig.getOnlineRecordConfig();
      if (onlineRecordConfig != null) {
        updateOnlineRecord(context, clients, onlineRecordConfig);
      }
    }
  }

  private void updateOnlineRecord(Ts3BotContext context, int clients,
                                  UserCounterPluginConfig.UserCounterConfig.UserCounterOnlineRecordConfig onlineRecordConfig) {
    TS3Api api = context.getApi();
    ChannelBase onlineRecordChannel = tsApiUtils.findUniqueMandatoryChannel(api, onlineRecordConfig.getChannelSearchString());
    int databaseId = context.getSessionInfo().getDatabaseId();
    Map<String, String> customClientProperties = api.getCustomClientProperties(databaseId);
    String recordCountClientsStr = customClientProperties.get(onlineRecordConfig.getOnlineRecordId());

    int recordCountClientsOld = 0;
    if (recordCountClientsStr != null && recordCountClientsStr.isEmpty() == false) {
      recordCountClientsOld = Integer.parseInt(recordCountClientsStr);
    }

    int recordCountClientsNew = recordCountClientsOld;

    if (recordCountClientsOld < clients) {
      customClientProperties.put(onlineRecordConfig.getOnlineRecordId(), Integer.toString(clients));
      api.setCustomClientProperties(databaseId, customClientProperties);
      recordCountClientsNew = clients;
    }

    String oldChannelName = onlineRecordChannel.getName();
    String channelNameTemplate = onlineRecordConfig.getChannelNameTemplate();

    String newChannelName = new MessageFormatingBuilder()
        .addUserCount(recordCountClientsNew)
        .build(channelNameTemplate);

    if (oldChannelName.equals(newChannelName) == false) {
      api.editChannel(onlineRecordChannel.getId(), ChannelProperty.CHANNEL_NAME, newChannelName);
    }
  }
}
