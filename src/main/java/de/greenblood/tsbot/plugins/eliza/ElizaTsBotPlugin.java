package de.greenblood.tsbot.plugins.eliza;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import de.greenblood.eliza.Eliza;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;
import de.greenblood.tsbot.TsBotPlugin;

/**
 * Created by Greenblood on 14.04.2019.
 */
public class ElizaTsBotPlugin implements TsBotPlugin {
    private TsApiUtils tsApiUtils = new TsApiUtils();
    private final Eliza eliza;

    public ElizaTsBotPlugin() {
        this.eliza = new Eliza(new UrlScriptLoader("script.txt"));
    }

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    }


    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e) {

        if (tsApiUtils.isPrivateMessageForBot(e, context.getSessionInfo()) && tsApiUtils.isNotMessageFromMyself(e, context.getSessionInfo())) {
            String answer = eliza.processInput(e.getMessage());
            context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, e.getInvokerId(), answer);
        }
    }

    @Override
    public void init(Ts3BotContext context) {

    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {

    }
}
