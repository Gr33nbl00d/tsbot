package de.greenblood.tsbot.plugins.vpnprotection;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.greenblood.tsbot.caches.ClientInfoRetriever;
import de.greenblood.tsbot.common.*;
import de.greenblood.tsbot.plugins.vpnprotection.provider.BlackListCheckResult;
import de.greenblood.tsbot.plugins.vpnprotection.provider.BlackListProvider;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class VpnProtectionPlugin extends UpdatableTsBotPlugin<VpnProtectionPluginConfig> {

  private Map<String, BlackListCheckResult> blackListCheckCache;
  private Ts3BotContext context;
  @Autowired
  private VpnProtectionPluginConfig vpnProtectionPluginConfig;
  private HashSet<String> whiteList;
  @Autowired
  private BeanUtil beanUtil;
  private BlackListProvider blackListProvider;
  private Logger logger = LoggerFactory.getLogger(VpnProtectionPlugin.class);

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    ClientInfo clientInfo = ClientInfoRetriever.getInstance().retrieve(context, e.getClientId(), true);
    String ip = clientInfo.getIp();
    if (whiteList.contains(ip)) {
      logger.info("client {} with ip {} not checked because ip is white listed", clientInfo.getNickname(), ip);
      return;
    }
    BlackListCheckResult blackListCheckResult = blackListCheckCache.get(ip);
    if (blackListCheckResult == null) {
      blackListCheckResult = blackListProvider.isBlacklistedIp(ip);
      blackListCheckCache.put(ip, blackListCheckResult);
    }

    if (blackListCheckResult.isBlackListed()) {
      logger.info("kicking client {} with ip {} because of black list", clientInfo.getNickname(), ip);
      String message = new MessageFormatingBuilder()
          .addClient(clientInfo)
          .build(vpnProtectionPluginConfig.getKickMessage());
      context.getAsyncApi().kickClientFromServer(message, e.getClientId());
    }
  }

  @Override
  public Class<VpnProtectionPluginConfig> getConfigClass() {
    return VpnProtectionPluginConfig.class;
  }


  @Override
  public void init(Ts3BotContext context) {
    blackListCheckCache = new LRUMap<>(vpnProtectionPluginConfig.getIpCacheSize());
    this.whiteList = new HashSet<>(vpnProtectionPluginConfig.getWhiteList());
    String blackListProviderClassName = vpnProtectionPluginConfig.getBlackListProvider();
    try {
      Object bean = beanUtil.getBean(Class.forName(blackListProviderClassName));
      this.blackListProvider = (BlackListProvider) bean;
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("black list provider not found: " + blackListProviderClassName, e);
    }
  }

  @Override
  public void reloadPlugin(Ts3BotContext context) {
    this.init(context);
  }

  @Override
  public String getReadWriteAuthorityName() {
    return "vpnprotection_maintainer";
  }
}
