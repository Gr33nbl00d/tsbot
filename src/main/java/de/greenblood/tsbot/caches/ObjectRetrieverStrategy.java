package de.greenblood.tsbot.caches;

import de.greenblood.tsbot.common.Ts3BotContext;

public interface ObjectRetrieverStrategy<K, T> {

  T retrieve(Ts3BotContext context, K objectId);
}
