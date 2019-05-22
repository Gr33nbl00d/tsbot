package de.greenblood.tsbot.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.greenblood.tsbot.common.Ts3BotContext;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientsOnlineRetriever {

  private final static ClientsOnlineRetriever instance = new ClientsOnlineRetriever();
  private long lastUpdateTime;
  private List<Client> clients;
  private boolean invalidated;

  public ClientsOnlineRetriever() {
  }

  public static ClientsOnlineRetriever getInstance() {
    return instance;
  }

  public synchronized List<Client> getClients(Ts3BotContext context, long maxAge) {
    long currentTimeMillis = System.currentTimeMillis();
    if (clients == null || invalidated == true || (currentTimeMillis - lastUpdateTime) > maxAge) {
      updateCache(context);
      this.invalidated = false;
      this.lastUpdateTime = currentTimeMillis;
    }
    return clients;
  }

  private void updateCache(Ts3BotContext context) {
    this.clients = null;
    this.clients = context.getApi().getClients();
  }

  public synchronized void invalidate() {
    this.invalidated = true;
  }

  public List<Client> getFilteredClients(Ts3BotContext ts3BotContext, int maxAge, Predicate<? super Client> filter) {
    List<Client> clients = this.getClients(ts3BotContext, maxAge);
    return clients.stream().filter(filter).collect(Collectors.toList());
  }
}
