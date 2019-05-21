package de.greenblood.tsbot.plugins.userlist;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;

import de.greenblood.tsbot.common.DefaultTsBotPlugin;
import de.greenblood.tsbot.common.MessageFormattingUtil;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class UserListPlugin extends DefaultTsBotPlugin {
  
  @Autowired(required = false)
  private UserListPluginConfig userListPluginConfig;
  private TsApiUtils tsApiUtils = new TsApiUtils();
  private final MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();
  private Map<Integer, String> channelIdToChannelTemplateDescriptionMap = new HashMap<>();
  private Map<String, ChannelBase> channelSearchStringToChannelMap = new HashMap<>();

  public UserListPlugin() {
  }

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    updateUserLists(context);
  }

  @Override
  public void onClientLeave(Ts3BotContext context, ClientLeaveEvent e) {
    updateUserLists(context);
  }


  private void updateUserLists(Ts3BotContext context) {
    List<Client> clientsOnline = context.getApi().getClients();

    for (UserListPluginConfig.UserListConfig userListConfig : userListPluginConfig.getUserListConfigList()) {

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
  }


  @Override
  public void init(Ts3BotContext context) {
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

  /*

    private static void updateTeamChannelDescription(TS3Api api) {
      List<Client> clientOnline = api.getClients();
      for (UserListPluginConfig.UserListConfig userListConfig : userList) {
        try {

          for (Map.Entry<Integer, TeamListServerGroupConfig> entry : servergroupIdtoServerGroupConfigMap.entrySet()) {
            Integer serverGroup = entry.getKey();
            String textToReplace = "%SERVER_GROUP_MEMBER_ENTRIES_" + serverGroup + "%";
            String replacingText = getTextEntriesForServerGroup(api, clientOnline, serverGroup, entry.getValue());
            userListChannelDescription = userListChannelDescription.replace(textToReplace, replacingText);
          }

          final Map<ChannelProperty, String> properties = new HashMap<>();
          properties.put(ChannelProperty.CHANNEL_DESCRIPTION, userListChannelDescription);

          api.editChannel(CHANNEL1, properties);

        } catch (IOException e) {
          throw new IllegalStateException("teamlist text file is missing", e);
        }
      }
    }

    private static void readConfig() {
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_INHABER, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=#1090ff]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_SERVER_ADMIN, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=darkred]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_COMMUNITY_ADMIN, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=OrangeRed]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_SUPPORTER, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=green]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_DEVELOPER, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=grey]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_DEVELOPER2, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=grey]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
      servergroupIdtoServerGroupConfigMap.put(SERVER_GROUP_TEST_SUPPORTER, new TeamListServerGroupConfig(
          "      [URL=client://0/%UNIQUECLIENTIDENTIFIER%~][color=green]%NICKNAME%[/color][/URL] | %ONLINESTATUS%"));
    }
  */

  private String getTextEntriesForServerGroup(Ts3BotContext context, List<Client> clientOnline,
                                              UserListPluginConfig.UserList userList, UserListPluginConfig.UserListConfig userListConfig) {
    String entriesForServerGroup = "";

    List<Integer> serverGroupsToInclude = userList.getServerGroupsToInclude();
    List<ServerGroupClient> serverGroupClients = new ArrayList<>();
    for (Integer serverGroup : serverGroupsToInclude) {
      serverGroupClients.addAll(context.getApi().getServerGroupClients(serverGroup));
    }

    for (ServerGroupClient client : serverGroupClients) {
      String onlineStatusText = "";
      if (isClientOnline(clientOnline, client.getUniqueIdentifier())) {
        onlineStatusText = userListConfig.getOfflineHtmlTemplate();
        ;
      } else {
        onlineStatusText = userListConfig.getOnlineHtmlTemplate();
      }
      String entryHtmlTemplate = userList.getEntryHtmlTemplate();
      entryHtmlTemplate = messageFormattingUtil.formateOnlineStatus(entryHtmlTemplate, onlineStatusText);
      entryHtmlTemplate = messageFormattingUtil.format2(entryHtmlTemplate, client);
      entriesForServerGroup += entryHtmlTemplate;
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

}
