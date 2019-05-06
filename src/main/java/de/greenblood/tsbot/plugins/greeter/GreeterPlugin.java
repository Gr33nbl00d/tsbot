package de.greenblood.tsbot.plugins.greeter;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.TsBotPlugin;
import org.springframework.stereotype.Component;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class GreeterPlugin implements TsBotPlugin {

    public GreeterPlugin() {
    }

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
        TS3ApiAsync asyncApi = context.getAsyncApi();
        asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), "Hi the support is informed and will soon be there for you to help you register");
        asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), "In the meantime we could might chat a bit if you like...");
        asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), "Im the bot of Greenblood but he is quite busy all the time... so i feel a bit lonely sometimes");
    }

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e) {

    }

    @Override
    public void init(Ts3BotContext context) {

    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {

    }

}
