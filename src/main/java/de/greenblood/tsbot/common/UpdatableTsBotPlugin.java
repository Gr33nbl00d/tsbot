package de.greenblood.tsbot.common;

import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;

public abstract class UpdatableTsBotPlugin<T extends UpdateablePluginConfig<T>> extends DefaultTsBotPlugin<T> {
    public abstract void reloadPlugin(Ts3BotContext context);

    public abstract String getReadWriteAuthorityName();
}
