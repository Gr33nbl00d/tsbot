package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;

import java.util.HashMap;
import java.util.Map;

public class MessageFormatingBuilder {

  public static final String MAXCLIENTS = "%MAXCLIENTS%";
  public static final String NICKNAME = "%NICKNAME%";
  public static final String USER_COUNT = "%USER_COUNT%";
  public static final String ONLINESTATUS = "%ONLINESTATUS%";
  public static final String IP = "%IP%";
  public static final String COMMAND = "%COMMAND%";

  Map<String, String> replacementValues = new HashMap<>();

  public MessageFormatingBuilder addClient(Client client) {
    replacementValues.put(NICKNAME, client.getNickname());
    replacementValues.put(IP, client.getIp());
    return this;
  }
  public MessageFormatingBuilder addClient(ServerGroupClient client) {
    replacementValues.put(NICKNAME, client.getNickname());
    return this;
  }

  public MessageFormatingBuilder addUserCount(int userCount) {
    this.replacementValues.put(USER_COUNT, Integer.toString(userCount));
    return this;
  }

  public MessageFormatingBuilder addChatCommand(String command)
  {
    this.replacementValues.put(COMMAND, command);
    return this;
  }

  public MessageFormatingBuilder addMaxClients(int userCount) {
    this.replacementValues.put(MAXCLIENTS, Integer.toString(userCount));
    return this;
  }

  public MessageFormatingBuilder addOnlineStatus(String onlineStatus) {
    this.replacementValues.put(ONLINESTATUS, onlineStatus);
    return this;
  }

  public String build(String template) {

    for (Map.Entry<String, String> entry : replacementValues.entrySet()) {

      template = template.replace(entry.getKey(), entry.getValue());
    }
    return template;
  }

}
