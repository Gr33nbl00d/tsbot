package de.greenblood.tsbot.caches;

import de.greenblood.tsbot.common.Ts3BotContext;

import org.apache.commons.collections4.map.PassiveExpiringMap;

public class TsExpiringCacheObjectRetriever<K, T> {

  private PassiveExpiringMap<K, T> cache;
  private ObjectRetrieverStrategy<K, T> objectRetrieverStrategy;

  public TsExpiringCacheObjectRetriever(long expiringTime, ObjectRetrieverStrategy<K, T> objectRetrieverStrategy) {
    cache = new PassiveExpiringMap<>(expiringTime);
    this.objectRetrieverStrategy = objectRetrieverStrategy;
  }

  public T retrieve(Ts3BotContext context, K objectId, boolean useCache) {
    T object = null;
    if (useCache) {
      object = cache.get(objectId);
    }
    if (object == null) {
      object = objectRetrieverStrategy.retrieve(context, objectId);
      cache.put(objectId, object);
    }
    return object;
  }
}
