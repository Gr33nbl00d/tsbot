package de.greenblood.tsbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelBase;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import de.greenblood.tsbot.common.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Ts3Bot extends TS3EventAdapter {


    private Counter disconnectCounter;
    private Counter textMessagesCounter;
    @Autowired
    private TsBotConfig config;
    @Autowired
    private TS3ServerConfig serverConfig;
    private Ts3BotContext context;

    private Logger logger = LoggerFactory.getLogger(Ts3Bot.class);
    private TsApiUtils tsApiUtils = new TsApiUtils();
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private MeterRegistry meterRegistry;
    private TS3Query query;
    private static final Logger log = LoggerFactory.getLogger(Ts3Bot.class);


    public Ts3Bot() {

    }

    public void connect() {
        TS3Config config = new TS3Config();
        config.setQueryPort(serverConfig.getQueryPort());
        config.setCommandTimeout(serverConfig.getCommandTimeout());
        config.setEnableCommunicationsLogging(serverConfig.isEnableCommunicationsLogging());
        config.setHost(serverConfig.getHost());
        config.setReconnectStrategy(ReconnectStrategy.constantBackoff(5000));
        config.setFloodRate(TS3Query.FloodRate.custom(serverConfig.getFloodRate()));
        config.setConnectionHandler(new ConnectionHandler() {
            @Override
            public void onConnect(TS3Query ts3Query) {
                logger.info("Connection to server established");
                initialize(ts3Query);
            }

            @Override
            public void onDisconnect(TS3Query ts3Query) {
                logger.warn("Connection to server lost");
                disconnectCounter.increment();
            }
        });
        this.query = new TS3Query(config);
        query.connect();
    }

    private void initialize(TS3Query query) {
        this.disconnectCounter = Counter.builder("odysseus.disconnectcount")
                .description("Number of disconnects from teamspeak server")
                .register(meterRegistry);

        this.textMessagesCounter = Counter.builder("odysseus.processedtextmessages")
                .description("Number of processed text messages from teamspeak server")
                .register(meterRegistry);

        final TS3Api api = query.getApi();
        final TS3ApiAsync asyncApi = query.getAsyncApi();
        //"testbot"
        //"Au88QvUp"
        api.login(this.config.getLoginName(), this.config.getLoginPassword());
        //TODO this is needed for production
        if (this.serverConfig.isSelectVirtualServerByPort()) {
            api.selectVirtualServerByPort(this.serverConfig.getVirtualServerIdentifier());
        } else {
            api.selectVirtualServerById(this.serverConfig.getVirtualServerIdentifier());
        }
        try {
            api.setNickname(this.config.getBotNickname());
        } catch (TS3CommandFailedException e) {
            //ignore this for now
        }

        //api.selectVirtualServerById(9020, config.getBotNickname());
        //api.registerAllEvents();
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.CHANNEL);
        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.sendServerMessage(this.config.getBotNickname() + " is online!");

        final ServerQueryInfo clientInfo = api.whoAmI();
        api.addTS3Listeners(this);
        this.context = new Ts3BotContext() {
            @Override
            public TS3ApiAsync getAsyncApi() {
                return asyncApi;
            }

            @Override
            public ServerQueryInfo getSessionInfo() {
                return clientInfo;
            }

            @Override
            public TS3Api getApi() {
                return api;
            }
        };

        if (config.getBotHomeChannelSearchString() != null) {
            ChannelBase botHomeChannel = tsApiUtils.findUniqueMandatoryChannel(context.getApi(), config.getBotHomeChannelSearchString());
            context.getApi().moveQuery(botHomeChannel);
        }

        for (TsBotPluginInterface tsBotPlugin : this.pluginManager.getTsBotPluginList()) {
            try {
                tsBotPlugin.init(context);
            } catch (Exception e) {
                log.error("Failed to load plugin", tsBotPlugin.getClass().getName(), e);
            }
        }
    }



    @Override
    public void onClientJoin(ClientJoinEvent e) {
        for (TsBotPluginInterface tsBotPlugin : pluginManager.getTsBotPluginList()) {
            try {
                tsBotPlugin.onClientJoin(context, e);
            } catch (Exception ex) {
                log.error("Failed to execute on client join on plugin: " + tsBotPlugin.getClass().getName(), ex);
            }
        }
    }


    @Override
    public void onTextMessage(TextMessageEvent e) {
        this.textMessagesCounter.increment();
        for (TsBotPluginInterface tsBotPlugin : pluginManager.getTsBotPluginList()) {
            try {
                tsBotPlugin.onTextMessage(context, e);
            } catch (Exception ex) {
                log.error("Failed to execute on text message on plugin: " + tsBotPlugin.getClass().getName(), ex);
            }
        }

    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        for (TsBotPluginInterface tsBotPlugin : pluginManager.getTsBotPluginList()) {
            try {
                tsBotPlugin.onClientMoved(context, e);
            } catch (Exception ex) {
                log.error("Failed to execute on client moved on plugin: " + tsBotPlugin.getClass().getName(), ex);
            }
        }
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        for (TsBotPluginInterface tsBotPlugin : pluginManager.getTsBotPluginList()) {
            try {
                tsBotPlugin.onClientLeave(context, e);
            } catch (Exception ex) {
                log.error("Failed to execute on client leave on plugin: " + tsBotPlugin.getClass().getName(), ex);
            }

        }
    }

    public void reloadPlugin(UpdatableTsBotPlugin plugin) {
        plugin.reloadPlugin(context);
    }

    public boolean isConnected() {
        return query.isConnected();
    }
}
