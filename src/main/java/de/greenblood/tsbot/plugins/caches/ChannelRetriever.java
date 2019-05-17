package de.greenblood.tsbot.plugins.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import de.greenblood.tsbot.common.Ts3BotContext;

public class ChannelRetriever extends TsExpiringCacheObjectRetriever<Integer, ChannelInfo>
{
    public ChannelRetriever()
    {
        super(60000, (Ts3BotContext context, Integer channelId) -> {
            return context.getApi().getChannelInfo(channelId);
        });
    }
}
