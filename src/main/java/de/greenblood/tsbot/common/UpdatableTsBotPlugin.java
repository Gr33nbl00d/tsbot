package de.greenblood.tsbot.common;

import de.greenblood.tsbot.restservice.exceptions.PluginWebRequestNotSupported;
import org.springframework.security.core.Authentication;

import java.util.Map;

public abstract class UpdatableTsBotPlugin<T extends UpdateablePluginConfig<T>> extends DefaultTsBotPlugin<T> {
    public abstract void reloadPlugin(Ts3BotContext context);

    public abstract String getReadWriteAuthorityName();

    public abstract UpdateablePluginConfig<T> getConfig();

    public Object getResource(Authentication auth, String resource, Map<String, String[]> params) {
        throw new PluginWebRequestNotSupported("get request not supporeted");
    }

    public void postResource(Authentication auth, String resource, Map<String, String[]> params) {
        throw new PluginWebRequestNotSupported("post request not supporeted");
    }

    public void putResource(Authentication auth, String resource, Map<String, String[]> params, String body) {
        throw new PluginWebRequestNotSupported("put request not supporeted");
    }

}
