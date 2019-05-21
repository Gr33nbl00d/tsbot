package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

import java.util.ArrayList;
import java.util.List;

public class TsApiUtils {

  private final MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();

  public boolean isNotMessageFromMyself(TextMessageEvent e, ServerQueryInfo clientInfo) {
    return isMessageFromMyself(e, clientInfo) == false;
  }

  private boolean isMessageFromMyself(TextMessageEvent e, ServerQueryInfo clientInfo) {
    if (e.getInvokerId() == clientInfo.getId()) {
      return true;
    } else {
      return false;
    }
  }


  public boolean isNewUser(ClientJoinEvent e, int newUserGroup) {
    List<Integer> groups = parseGroups(e.getClientServerGroups());
    if (groups.size() == 1 && groups.get(0).equals(newUserGroup)) {
      return true;
    } else {
      return false;
    }
  }

  private List<Integer> parseGroups(String groupsString) {
    String[] groupArray = groupsString.split(",");
    List<Integer> groupIdList = new ArrayList<Integer>();
    for (String groupIdStr : groupArray) {
      groupIdList.add(Integer.parseInt(groupIdStr));
    }
    return groupIdList;

  }

  public boolean isPrivateMessageForBot(TextMessageEvent e, ServerQueryInfo clientInfo) {
    return (e.getTargetMode() == TextMessageTargetMode.CLIENT) && (e.getTargetClientId() == clientInfo.getId());
  }

  public Channel findUniqueMandatoryChannel(TS3Api ts3Api, String channelSearchString, boolean useCache) {
    List<Channel> matchingChannelList = ts3Api.getChannelsByName(channelSearchString);
    if (matchingChannelList.size() > 1) {
      throw new RuntimeException("more than one channel found matching string " + channelSearchString);
    }
    if (matchingChannelList.size() == 0) {
      throw new RuntimeException("No Channel found matching " + channelSearchString);
    }
    return matchingChannelList.get(0);
  }
}
