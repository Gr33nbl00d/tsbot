package de.greenblood.tsbot.plugins.support;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.greenblood.tsbot.TsBotConfig;
import de.greenblood.tsbot.TsBotPlugin;
import de.greenblood.tsbot.common.MessageFormattingUtil;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;
import de.greenblood.tsbot.common.UserListCache;
import de.greenblood.tsbot.plugins.caches.ClientInfoRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class SupportPlugin implements TsBotPlugin
{
    private static final Logger log = LoggerFactory.getLogger(SupportPlugin.class);
    private TsApiUtils tsApiUtils = new TsApiUtils();
    private Channel registrationChannel;
    @Autowired
    private SupportPluginConfig supportPluginConfig;
    @Autowired
    private TsBotConfig botConfig;
    private Map<Integer, SupportPluginConfig.SupportChannelConfig> chanlIdTSupportChannelConfigMap = new HashMap<>();
    private MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();
    private ClientInfoRetriever clientInfoRetriever = new ClientInfoRetriever();

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e)
    {
        TS3ApiAsync asyncApi = context.getAsyncApi();
        if (tsApiUtils.isNewUser(e, supportPluginConfig.getRegistrationChannelConfig().getNewUserGroup()))
        {
            handleUnregisteredUser(context, e, asyncApi);
        }
    }

    private void handleUnregisteredUser(Ts3BotContext context, ClientJoinEvent e, TS3ApiAsync asyncApi)
    {
        List<Client> supportersToInform = UserListCache.getInstance().getFilteredClients(context, 30000, supportPluginConfig.getRegistrationChannelConfig().getServerGroupsToInform());
        sendWelcomeMessageToNewUser(context, e.getClientId(), supportersToInform);
        asyncApi.moveClient(e.getClientId(), registrationChannel.getId());

        String message = supportPluginConfig.getRegistrationChannelConfig().getSupporterMessage();

        informSupporter(context, supportersToInform, message);
    }

    private void informSupporter(Ts3BotContext context, List<Client> supportersToInform, String message)
    {
        for (Client client : supportersToInform)
        {
            context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, client.getId(), messageFormattingUtil.format(message, client));
        }
    }

    private void sendWelcomeMessageToNewUser(Ts3BotContext context, int clientId, List<Client> supportersToInform)
    {
        if (supportersToInform.isEmpty())
        {
            context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, clientId, supportPluginConfig.getRegistrationChannelConfig().getNoSuporterOnlineMessage());
        }
        else
        {
            context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, clientId, supportPluginConfig.getRegistrationChannelConfig().getGreetingMessage());
        }
    }

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e)
    {

    }

    @Override
    public void init(Ts3BotContext context)
    {
        this.registrationChannel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), supportPluginConfig.getRegistrationChannelConfig().getChannelName(), true);

        if (botConfig.getBotHomeChannel() != null)
        {
            Channel botHomeChannel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), botConfig.getBotHomeChannel(), true);
            context.getApi().moveQuery(botHomeChannel);
        }

        List<SupportPluginConfig.SupportChannelConfig> supportChannels = supportPluginConfig.getSupportChannels();
        if (supportChannels != null)
        {
            for (SupportPluginConfig.SupportChannelConfig supportChannel : supportChannels)
            {
                Channel channel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), supportChannel.getChannelName(), true);
                this.chanlIdTSupportChannelConfigMap.put(channel.getId(), supportChannel);
            }
        }

    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e)
    {
        SupportPluginConfig.SupportChannelConfig supportChannelConfig = this.chanlIdTSupportChannelConfigMap.get(e.getTargetChannelId());
        if (supportChannelConfig != null)
        {
            if (supportChannelConfig.getServerGroupsToIgnore().contains(e.getClientId()) || supportChannelConfig.getServerGroupsToInform().contains(e.getClientId()))
            {
                return;
            }
            ClientInfo client = clientInfoRetriever.retrieve(context, e.getClientId(), true);
            String message = messageFormattingUtil.format(supportChannelConfig.getSupporterMessage(), client);
            List<Client> supportersToInform = UserListCache.getInstance().getFilteredClients(context, 30000, supportChannelConfig.getServerGroupsToInform());
            sendWelcomeMessageToNewUser(context, e.getClientId(), supportersToInform);
            informSupporter(context, supportersToInform, message);
        }
    }


}
