package de.greenblood.tsbot.plugins.usercounter;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.greenblood.tsbot.Ts3Bot;
import de.greenblood.tsbot.caches.ClientsOnlineRetriever;
import de.greenblood.tsbot.common.*;
import de.greenblood.tsbot.plugins.support.IncludedInServerGroupFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class UserCounterPlugin extends UpdatableTsBotPlugin<UserCounterPluginConfig> {

    private static final Logger log = LoggerFactory.getLogger(Ts3Bot.class);
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
    public Class<UserCounterPluginConfig> getConfigClass() {
        return UserCounterPluginConfig.class;
    }


    @Override
    public void init(Ts3BotContext context) {
        updateCounters(context);
    }

    private void updateCounters(Ts3BotContext context) {
        List<UserCounterConfig> userCounterList = userCounterPluginConfig.getUserCounterList();
        for (UserCounterConfig userCounterConfig : userCounterList) {
            try {
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
                UserCounterConfig.UserCounterOnlineRecordConfig
                        onlineRecordConfig = userCounterConfig.getOnlineRecordConfig();
                if (onlineRecordConfig != null) {
                    updateOnlineRecord(context, clients, onlineRecordConfig);
                }
            } catch (Exception e) {
                log.error("failed to update user counter " + userCounterConfig.getChannelSearchString(), e);
            }
        }
    }

    private void updateOnlineRecord(Ts3BotContext context, int clients,
                                    UserCounterConfig.UserCounterOnlineRecordConfig onlineRecordConfig) {
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

    @Override
    public void reloadPlugin(Ts3BotContext context) {
        this.init(context);
    }

    @Override
    public String getReadWriteAuthorityName() {
        return "usercounter_maintainer";
    }
}
