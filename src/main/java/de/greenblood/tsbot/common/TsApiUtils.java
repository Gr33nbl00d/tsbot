package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TsApiUtils {

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

  public ChannelBase findUniqueMandatoryChannel(TS3Api ts3Api, String channelSearchString) {
    Pattern pattern = Pattern.compile("(NAMECONTAINS|NAMEEQUALS|CHANNELID)\\:(.*)");
    Matcher matcher = pattern.matcher(channelSearchString);
    if (matcher.matches()) {
      String logic = matcher.group(1);
      String parameter = matcher.group(2);
      return findUniqueMandatoryChannel(ts3Api, logic, parameter);
    } else {
      throw new IllegalStateException("search string invalid: " + channelSearchString);
    }
  }

  private ChannelBase findUniqueMandatoryChannel(TS3Api ts3Api, String logic, String parameter) {
    if (logic.equals("NAMEEQUALS")) {
      Channel channelByNameExact = ts3Api.getChannelByNameExact(parameter, false);
      if (channelByNameExact == null) {
        throw new IllegalStateException("Unable to find channel with name equal to " + parameter);
      }
      return channelByNameExact;
    } else if (logic.equals("NAMECONTAINS")) {
      List<Channel> matchingChannelList = ts3Api.getChannelsByName(parameter);
      if (matchingChannelList.size() > 1) {
        throw new RuntimeException("more than one channel found matching string " + parameter);
      }
      if (matchingChannelList.size() == 0) {
        throw new RuntimeException("No Channel found matching " + parameter);
      }
      return matchingChannelList.get(0);

    } else if (logic.equals("CHANNELID")) {
      return ts3Api.getChannelInfo(Integer.parseInt(parameter));
    } else {
      throw new IllegalStateException("channel search logic " + logic + " unknown");
    }
  }
}
