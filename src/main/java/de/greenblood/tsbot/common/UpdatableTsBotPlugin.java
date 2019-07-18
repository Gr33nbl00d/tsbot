package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;

public abstract class UpdatableTsBotPlugin<T extends UpdateablePluginConfig<T>> extends DefaultTsBotPlugin<T> {
    public abstract void reloadPlugin(Ts3BotContext context);

    public abstract String getReadWriteAuthorityName();
}
