package de.greenblood.tsbot.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.greenblood.tsbot.common.Ts3BotContext;

public class ClientInfoRetriever extends TsExpiringCacheObjectRetriever<Integer, ClientInfo> {

  private static final ClientInfoRetriever instance = new ClientInfoRetriever();

  public static ClientInfoRetriever getInstance() {
    return instance;
  }

  public ClientInfoRetriever() {
    super(60000, (Ts3BotContext context, Integer clientId) -> {
      return context.getApi().getClientInfo(clientId);
    });
  }
}
