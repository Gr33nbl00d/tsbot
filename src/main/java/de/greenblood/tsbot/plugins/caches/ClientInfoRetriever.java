package de.greenblood.tsbot.plugins.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.greenblood.tsbot.common.Ts3BotContext;

public class ClientInfoRetriever extends TsExpiringCacheObjectRetriever<Integer, ClientInfo>
{
    public ClientInfoRetriever()
    {
        super(60000, (Ts3BotContext context, Integer clientId) -> {
            return context.getApi().getClientInfo(clientId);
        });
    }
}
