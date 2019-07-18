package de.greenblood.tsbot.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import de.greenblood.tsbot.common.Ts3BotContext;

public class ChannelRetriever extends TsExpiringCacheObjectRetriever<Integer, ChannelInfo> {

  private static final ChannelRetriever instance = new ChannelRetriever();

  public static ChannelRetriever getInstance() {
    return instance;
  }

  public ChannelRetriever() {
    super(60000, (Ts3BotContext context, Integer channelId) -> {
      return context.getApi().getChannelInfo(channelId);
    });
  }
}
