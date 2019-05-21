package de.greenblood.tsbot.plugins.support;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.List;
import java.util.function.Predicate;

public class IncludedInServerGroupFilter implements Predicate<Client> {

  private List<Integer> inclusionList;

  public IncludedInServerGroupFilter(List<Integer> inclusionList) {
    this.inclusionList = inclusionList;
  }

  @Override
  public boolean test(Client client) {
    for (Integer serverGroup : inclusionList) {
      if (client.isInServerGroup(serverGroup)) {
        return true;
      }
    }
    return false;
  }
}
