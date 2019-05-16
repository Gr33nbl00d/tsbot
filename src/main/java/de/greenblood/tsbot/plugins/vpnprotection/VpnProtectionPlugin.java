package de.greenblood.tsbot.plugins.vpnprotection;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.greenblood.tsbot.TsBotPlugin;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.plugins.vpnprotection.detector.BlackListCheckResult;
import de.greenblood.tsbot.plugins.vpnprotection.detector.IPQualityScoreComVPNDetector;
import de.greenblood.tsbot.plugins.vpnprotection.detector.VpnDetector;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class VpnProtectionPlugin implements TsBotPlugin
{
    Map<String, BlackListCheckResult> blackListCheckCache;

    private Ts3BotContext context;

    public VpnProtectionPlugin()
    {
        blackListCheckCache = new LRUMap<>(5000);
    }

    @Override
    public void onClientJoin(Ts3BotContext context, ClientJoinEvent e)
    {
        CommandFuture<ClientInfo> clientInfo = context.getAsyncApi().getClientInfo(e.getClientId());
        clientInfo.onSuccess(new CommandFuture.SuccessListener<ClientInfo>()
        {
            @Override
            public void handleSuccess(ClientInfo clientInfo)
            {
                VpnDetector detector = new IPQualityScoreComVPNDetector("CcwrH10kpY4PRRlXL86BpbowU72pKfi5");
                String ip = clientInfo.getIp();
                BlackListCheckResult blackListCheckResult = blackListCheckCache.get(ip);
                if (blackListCheckResult == null)
                {
                    blackListCheckResult = detector.isBlacklistedIp(ip);
                    blackListCheckCache.put(ip, blackListCheckResult);
                }

                if (blackListCheckResult.isBlackListed())
                {
                    context.getAsyncApi().kickClientFromServer("You are using a VPN or Proxy", e.getClientId());
                }
            }
        });
    }

    @Override
    public void onTextMessage(Ts3BotContext context, TextMessageEvent e)
    {

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

    private <K, V> Map<K, V> createLRUMap(final int maxEntries)
    {
        return new LinkedHashMap<K, V>(maxEntries * 10 / 7, 0.7f, true)
        {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
            {
                return size() > maxEntries;
            }
        };
    }
}
