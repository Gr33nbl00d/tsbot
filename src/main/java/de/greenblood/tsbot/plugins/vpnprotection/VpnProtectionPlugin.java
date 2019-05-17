package de.greenblood.tsbot.plugins.vpnprotection;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.greenblood.tsbot.TsBotPlugin;
import de.greenblood.tsbot.common.BeanUtil;
import de.greenblood.tsbot.common.MessageFormattingUtil;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.plugins.caches.ClientInfoRetriever;
import de.greenblood.tsbot.plugins.vpnprotection.provider.BlackListCheckResult;
import de.greenblood.tsbot.plugins.vpnprotection.provider.BlackListProvider;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class VpnProtectionPlugin implements TsBotPlugin
{
    private Map<String, BlackListCheckResult> blackListCheckCache;
    private Ts3BotContext context;
    @Autowired
    private VpnProtectionPluginConfig vpnProtectionPluginConfig;
    private HashSet<String> whiteList;
    @Autowired
    private BeanUtil beanUtil;
    private BlackListProvider blackListProvider;
    private Logger logger = LoggerFactory.getLogger(VpnProtectionPlugin.class);
    private final MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();
    private ClientInfoRetriever clientInfoRetriever = new ClientInfoRetriever();

    public VpnProtectionPlugin()
    {
    }

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e)
    {
        ClientInfo clientInfo = clientInfoRetriever.retrieve(context, e.getClientId(), true);
        String ip = clientInfo.getIp();
        if (whiteList.contains(ip))
        {
            logger.info("client {} with ip {} not checked because ip is white listed", clientInfo.getNickname(), ip);
            return;
        }
        BlackListCheckResult blackListCheckResult = blackListCheckCache.get(ip);
        if (blackListCheckResult == null)
        {
            blackListCheckResult = blackListProvider.isBlacklistedIp(ip);
            blackListCheckCache.put(ip, blackListCheckResult);
        }

        if (blackListCheckResult.isBlackListed())
        {
            logger.info("kicking client {} with ip {} because of black list", clientInfo.getNickname(), ip);
            String kickMessage = messageFormattingUtil.format(vpnProtectionPluginConfig.getKickMessage(), clientInfo);
            context.getAsyncApi().kickClientFromServer(kickMessage, e.getClientId());
        }
    }

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e)
    {

    }

    @PostConstruct
    public void postConstruct()
    {
        blackListCheckCache = new LRUMap<>(vpnProtectionPluginConfig.getIpCacheSize());
        this.whiteList = new HashSet<>(vpnProtectionPluginConfig.getWhiteList());
        String blackListProviderClassName = vpnProtectionPluginConfig.getBlackListProvider();
        try
        {
            Object bean = beanUtil.getBean(Class.forName(blackListProviderClassName));
            this.blackListProvider = (BlackListProvider) bean;
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("black list provider not found: " + blackListProviderClassName, e);
        }
    }

    @Override
    public void init(Ts3BotContext context)
    {
        this.context = context;
    }

    @Override
    public void onClientMoved(Ts3BotContext context, ClientMovedEvent e)
    {

    }
}
