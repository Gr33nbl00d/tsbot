package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;

public class MessageFormattingUtil {

  public String format(String message, Client client) {
    message = message.replaceAll("\\%IP\\%", client.getIp());
    message = message.replaceAll("\\%NICKNAME\\%", client.getNickname());
    return message;
  }

  public String formatCommand(String message, String commandName) {
    return message.replaceAll("\\%COMMAND\\%", commandName);
  }

  public String formatNewChannel(String message, Client client, int maxClients) {
    message = message.replaceAll("\\%MAXCLIENTS\\%", Integer.toString(maxClients));
    return this.formatNewChannel(message, client);
  }

  public String formatNewChannel(String message, Client client) {
    message = message.replaceAll("\\%NICKNAME\\%", client.getNickname());
    return message;
  }

  public String formatNewChannel(String message, int userCount) {
    return message.replaceAll("\\%USER_COUNT\\%", Integer.toString(userCount));
  }

  public String format2(String entryHtmlTemplate, ServerGroupClient client) {
    return entryHtmlTemplate.replace("%NICKNAME%", client.getNickname());
  }

  public String formateOnlineStatus(String entryHtmlTemplate, String onlineStatusText) {
    return entryHtmlTemplate.replace("%ONLINESTATUS%", onlineStatusText);
  }
}
