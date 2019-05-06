package de.greenblood.tsbot.plugins.support;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import de.greenblood.tsbot.TsBotConfig;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;
import de.greenblood.tsbot.TsBotPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class SupportPlugin implements TsBotPlugin {
    private static final Logger log = LoggerFactory.getLogger(SupportPlugin.class);
    private TsApiUtils tsApiUtils = new TsApiUtils();
    private Channel registrationChannel;
    @Autowired
    private SupportPluginConfig supportPluginConfig;
    @Autowired
    private TsBotConfig botConfig;

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
        TS3ApiAsync asyncApi = context.getAsyncApi();
        if (tsApiUtils.isNewUser(e, supportPluginConfig.getNewUserGroup())) {
            asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), supportPluginConfig.getNewUserGreetingText());
            asyncApi.moveClient(e.getClientId(), registrationChannel.getId());
            tsApiUtils.informAllOnlineMembersAboutNewUser(asyncApi, e.getClientNickname(), supportPluginConfig.getServerGroupsToInform(), supportPluginConfig.getSupporterMessageText());
        }

    }

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e) {

    }

    @Override
    public void init(Ts3BotContext context) {
        List<Channel> channelsByName = context.getApi().getChannelsByName(supportPluginConfig.getRegistrationChannelName());
        if (channelsByName.size() > 1)
            throw new RuntimeException("more than one registration channel found");
        if (channelsByName.size() == 0) {
            throw new RuntimeException("registration channel not found");
        }

        this.registrationChannel = channelsByName.get(0);
        if (botConfig.getBotHomeChannel() != null) {
            List<Channel> botHomeChannelList = context.getApi().getChannelsByName(botConfig.getBotHomeChannel());
            if (botHomeChannelList.size() == 0) {
                log.warn("bot home channel not found");
            } else {
                Channel bots = botHomeChannelList.get(0);
                context.getApi().moveQuery(bots);
            }
        }
    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {

    }
}
