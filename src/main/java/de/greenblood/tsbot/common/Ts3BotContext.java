package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

/**
 * Created by Greenblood on 14.04.2019.
 */
public interface Ts3BotContext {
    TS3ApiAsync getAsyncApi();

    ServerQueryInfo getSessionInfo();

    TS3Api getApi();
}
