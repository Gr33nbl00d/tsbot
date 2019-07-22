package de.greenblood.tsbot.plugins.userlist;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;
import de.greenblood.tsbot.Ts3Bot;
import de.greenblood.tsbot.caches.ClientsOnlineRetriever;
import de.greenblood.tsbot.caches.ServerGroupClientsRetriever;
import de.greenblood.tsbot.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class UserListPlugin extends UpdatableTsBotPlugin<UserListPluginConfig> {

    private static final Logger log = LoggerFactory.getLogger(Ts3Bot.class);
    @Autowired(required = false)
    private UserListPluginConfig userListPluginConfig;
    private TsApiUtils tsApiUtils = new TsApiUtils();

    private Map<String, ChannelBase> channelSearchStringToChannelMap;
    //online clients which causing changes to the user list once they leave
    //this way we can detect if we need to update the user list on client leave
    private Map<Integer, HashSet<String>> clientIdToUserListsMap;
    private Map<Integer, String> channelIdToChannelTemplateDescriptionMap;

    public UserListPlugin() {
    }

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {

        String clientServerGroupsString = e.getClientServerGroups();
        List<Integer> clientServerGroups = convertClientServerGroupStringToIntArray(clientServerGroupsString);
        updateListsIfNeeded(context, clientServerGroups, e.getClientId());
    }

    private void updateListsIfNeeded(Ts3BotContext context, List<Integer> clientServerGroups, Integer clientId) {
        List<Client> clientsOnline = null;
        for (UserListPluginConfig.UserListConfig userListConfig : userListPluginConfig.getUserListConfigList()) {
            List<Integer> allServerGroupsInUserList = getAllServerGroupsInUserList(userListConfig);
            if (isInAtLeastOneServerGroup(allServerGroupsInUserList, clientServerGroups)) {
                if (clientsOnline == null) {
                    clientsOnline = ClientsOnlineRetriever.getInstance().getClients(context, 0);
                }
                clientIdToUserListsMap.computeIfAbsent(clientId, emptyList -> new HashSet<String>()).add(userListConfig.getChannelSearchString());
                updateUserList(context, clientsOnline, userListConfig);
            }
        }
    }

    private boolean isInAtLeastOneServerGroup(List<Integer> allServerGroupsInUserList, List<Integer> clientServerGroups) {
        for (Integer clientServerGroup : clientServerGroups) {
            if (allServerGroupsInUserList.contains(clientServerGroup)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> convertClientServerGroupStringToIntArray(String clientServerGroupsString) {
        String[] clientServerGroupsStringArray = clientServerGroupsString.split(",");
        List<Integer> clientServerGroups = new ArrayList<>();
        for (String clientServerGroupStr : clientServerGroupsStringArray) {
            clientServerGroups.add(Integer.valueOf(clientServerGroupStr));
        }
        return clientServerGroups;
    }

    @Override
    public void onClientLeave(Ts3BotContext context, ClientLeaveEvent e) {

        HashSet<String> searchStringValuesOfInfluencedUserLists = clientIdToUserListsMap.get(e.getClientId());
        if (searchStringValuesOfInfluencedUserLists != null) {
            List<Client> clientsOnline = null;
            for (UserListPluginConfig.UserListConfig userListConfig : this.userListPluginConfig.getUserListConfigList()) {
                String searchString = userListConfig.getChannelSearchString();
                if (searchStringValuesOfInfluencedUserLists.contains(searchString)) {
                    if (clientsOnline == null) {
                        clientsOnline = ClientsOnlineRetriever.getInstance().getClients(context, 0);
                    }
                    searchStringValuesOfInfluencedUserLists.remove(searchString);
                    if (searchStringValuesOfInfluencedUserLists.isEmpty()) {
                        clientIdToUserListsMap.remove(e.getClientId());
                    }
                    updateUserList(context, clientsOnline, userListConfig);
                }
            }


        }
    }

    @Override
    public Class<UserListPluginConfig> getConfigClass() {
        return UserListPluginConfig.class;
    }

    private void updateUserLists(Ts3BotContext context) {
        List<Client> clientsOnline = ClientsOnlineRetriever.getInstance().getClients(context, 0);
        for (UserListPluginConfig.UserListConfig userListConfig : userListPluginConfig.getUserListConfigList()) {
            updateUserList(context, clientsOnline, userListConfig);
        }
    }

    private List<Integer> getAllServerGroupsInUserList(UserListPluginConfig.UserListConfig userListConfig) {
        List<Integer> allServerGroupsInUserList = new ArrayList<>();
        for (UserListPluginConfig.UserList userList : userListConfig.getUserList()) {
            allServerGroupsInUserList.addAll(userList.getServerGroupsToInclude());
        }
        return allServerGroupsInUserList;
    }

    private void updateUserList(Ts3BotContext context, List<Client> clientsOnline, UserListPluginConfig.UserListConfig userListConfig) {
        ChannelBase channel = channelSearchStringToChannelMap.get(userListConfig.getChannelSearchString());
        String userListChannelDescription = channelIdToChannelTemplateDescriptionMap.get(channel.getId());
        for (UserListPluginConfig.UserList userList : userListConfig.getUserList()) {
            String userListIdentifier = userList.getIdentifier();
            String textToReplace = "%USER_LIST_" + userListIdentifier + "%";
            String replacingText = getTextEntriesForServerGroup(context, clientsOnline, userList, userListConfig);
            userListChannelDescription = userListChannelDescription.replace(textToReplace, replacingText);
        }
        context.getAsyncApi().editChannel(channel.getId(), ChannelProperty.CHANNEL_DESCRIPTION, userListChannelDescription);
    }


    @Override
    public void init(Ts3BotContext context) {
        this.channelIdToChannelTemplateDescriptionMap = new HashMap<>();
        this.clientIdToUserListsMap = new HashMap<>();
        this.channelSearchStringToChannelMap = new HashMap<>();
        List<UserListPluginConfig.UserListConfig> userListConfigList = userListPluginConfig.getUserListConfigList();
        for (UserListPluginConfig.UserListConfig userListConfig : userListConfigList) {
            String templateFileLocation = userListConfig.getTemplateFileLocation();
            try {
                File templateFile = new File(templateFileLocation);
                ChannelBase channel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), userListConfig.getChannelSearchString());
                channelIdToChannelTemplateDescriptionMap.put(channel.getId(), new String(Files.readAllBytes(templateFile.toPath()), "UTF-8"));
                channelSearchStringToChannelMap.put(userListConfig.getChannelSearchString(), channel);
            } catch (IOException e) {
                throw new IllegalStateException("could not read template file" + templateFileLocation);
            }
        }
        updateUserLists(context);
    }

    private String getTextEntriesForServerGroup(Ts3BotContext context, List<Client> clientOnline,
                                                UserListPluginConfig.UserList userList, UserListPluginConfig.UserListConfig userListConfig) {
        String entriesForServerGroup = "";

        List<Integer> serverGroupsToInclude = userList.getServerGroupsToInclude();
        List<ServerGroupClient> serverGroupClients = new ArrayList<>();
        for (Integer serverGroup : serverGroupsToInclude) {

            try {
                List<ServerGroupClient> serverGroupsOfClient = ServerGroupClientsRetriever.getInstance().retrieve(context, serverGroup, true);
                serverGroupClients.addAll(serverGroupsOfClient);

                //TODO better concept needed for failed commands
            } catch (TS3CommandFailedException e) {
                log.error("could not retrieve users for server group " + serverGroup, e);
            }
        }

        for (ServerGroupClient client : serverGroupClients) {
            String onlineStatusText = "";
            if (isClientOnline(clientOnline, client.getUniqueIdentifier())) {
                onlineStatusText = userListConfig.getOfflineHtmlTemplate();
            } else {
                onlineStatusText = userListConfig.getOnlineHtmlTemplate();
            }
            String entryHtmlTemplate = userList.getEntryHtmlTemplate();
            String htmlEntry = new MessageFormatingBuilder()
                    .addOnlineStatus(onlineStatusText)
                    .addClient(client)
                    .build(entryHtmlTemplate);

            entriesForServerGroup += htmlEntry + "\n";
        }

        return entriesForServerGroup;
    }

    private static boolean isClientOnline(List<Client> clients, String uniqueIdentifier) {
        for (Client client : clients) {
            if (client.getUniqueIdentifier().equals(uniqueIdentifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reloadPlugin(Ts3BotContext context) {
        init(context);
    }

    @Override
    public String getReadWriteAuthorityName() {
        return "userlist_maintainer";
    }
}
