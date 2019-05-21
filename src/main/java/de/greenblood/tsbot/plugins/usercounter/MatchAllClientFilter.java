package de.greenblood.tsbot.plugins.usercounter;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.function.Predicate;

public class MatchAllClientFilter implements Predicate<Client> {

  @Override
  public boolean test(Client client) {
    return true;
  }
}
