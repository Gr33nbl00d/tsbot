package de.greenblood.tsbot.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;

import de.greenblood.tsbot.common.Ts3BotContext;

import java.util.List;

public class ServerGroupClientsRetriever extends TsExpiringCacheObjectRetriever<Integer, List<ServerGroupClient>> {

  private static final ServerGroupClientsRetriever instance = new ServerGroupClientsRetriever();

  public static ServerGroupClientsRetriever getInstance() {
    return instance;
  }

  public ServerGroupClientsRetriever() {
    super(60000, (Ts3BotContext context, Integer serverGroupId) -> {
      return context.getApi().getServerGroupClients(serverGroupId);
    });
  }

}
