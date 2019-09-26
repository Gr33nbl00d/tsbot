package de.greenblood.tsbot.caches;

import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;
import de.greenblood.tsbot.common.Ts3BotContext;

import java.util.List;

public class ServerGroupsRetriever extends TsExpiringCacheObjectRetriever<Void, List<ServerGroup>> {

    private static final ServerGroupsRetriever instance = new ServerGroupsRetriever();

    public static ServerGroupsRetriever getInstance() {
        return instance;
    }

    public ServerGroupsRetriever() {
        super(60000, (Ts3BotContext context, Void noParam) -> {
            return context.getApi().getServerGroups();
        });
    }

}
