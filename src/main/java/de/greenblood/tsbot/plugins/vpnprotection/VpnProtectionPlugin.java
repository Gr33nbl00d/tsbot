package de.greenblood.tsbot.plugins.vpnprotection;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.google.gson.Gson;
import de.greenblood.tsbot.caches.ClientInfoRetriever;
import de.greenblood.tsbot.common.*;
import de.greenblood.tsbot.common.UpdateablePluginConfig;
import de.greenblood.tsbot.plugins.vpnprotection.provider.BlackListCheckResult;
import de.greenblood.tsbot.plugins.vpnprotection.provider.BlackListProvider;
import de.greenblood.tsbot.restservice.AuthorityChecker;
import de.greenblood.tsbot.restservice.ConfigPrefixPatcher;
import de.greenblood.tsbot.restservice.YamlConfigStringConverter;
import de.greenblood.tsbot.restservice.exceptions.AccessDeniedException;
import de.greenblood.tsbot.restservice.exceptions.InvalidPluginConfigurationException;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class VpnProtectionPlugin extends UpdatableTsBotPlugin<VpnProtectionPluginConfig> {

    private Map<String, BlackListCheckResult> blackListCheckCache;
    @Autowired
    private VpnProtectionPluginConfig vpnProtectionPluginConfig;
    private HashSet<String> whiteList;
    @Autowired
    private BeanUtil beanUtil;
    private BlackListProvider activeblackListProvider;
    @Autowired
    private List<BlackListProvider> availableBlackListProviders;
    @Autowired
    private AuthorityChecker authorityChecker;
    @Autowired
    YamlConfigStringConverter yamlConfigStringConverter;


    private Logger logger = LoggerFactory.getLogger(VpnProtectionPlugin.class);
    private final Gson gson = new Gson();
    private ConfigPrefixPatcher configPrefixPatcher = new ConfigPrefixPatcher();

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
            blackListCheckResult = activeblackListProvider.isBlacklistedIp(ip);
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
        String blackListProviderSimpleClassName = vpnProtectionPluginConfig.getBlackListProvider();

        this.activeblackListProvider = getBlacklistProviderBySimpleClassName(blackListProviderSimpleClassName);
    }

    private BlackListProvider getBlacklistProviderBySimpleClassName(String blackListProviderSimpleClassName) {
        for (BlackListProvider availableBlackListProvider : this.availableBlackListProviders) {
            if (availableBlackListProvider.getClass().getSimpleName().equals(blackListProviderSimpleClassName)) {
                return availableBlackListProvider;
            }
        }
        throw new IllegalStateException("provider not found " + blackListProviderSimpleClassName);
    }

    @Override
    public void reloadPlugin(Ts3BotContext context) {
        this.init(context);
    }

    @Override
    public String getReadWriteAuthorityName() {
        return "vpnprotection_maintainer";
    }

    @Override
    public UpdateablePluginConfig<VpnProtectionPluginConfig> getConfig() {
        return this.vpnProtectionPluginConfig;
    }

    @Override
    public Object getResource(Authentication auth, String resource, Map<String, String[]> params) {
        if (authorityChecker.hasAuthority(auth, getReadWriteAuthorityName()) == false) {
            throw new AccessDeniedException("no access rights to access this plugin");
        }

        if ("blacklistproviderconfig".equals(resource)) {
            String simpleClassName = getProviderClassFromParams(params);
            BlackListProvider blackListProvider = getBlackListProviderInstanceByClass(simpleClassName);
            return blackListProvider.getConfig();

        } else if ("availableblacklistprovider".equals(resource)) {
            List<BlackListProviderInfo> blackListProviderInfos = new ArrayList<>();
            for (BlackListProvider blackListProvider : this.availableBlackListProviders) {

                blackListProviderInfos.add(new BlackListProviderInfo(blackListProvider.getProviderName(), blackListProvider.getClass().getSimpleName()));
            }
            return blackListProviderInfos;
        } else {
            return super.getResource(auth, resource, params);
        }
    }

    private BlackListProvider getBlackListProviderInstanceByClass(String aClass) {
        for (BlackListProvider availableBlackListProvider : this.availableBlackListProviders) {
            if (availableBlackListProvider.getClass().getSimpleName().equals(aClass)) {
                return availableBlackListProvider;
            }
        }
        throw new InvalidPluginConfigurationException("blacklist provider not found");
    }

    private String getProviderClassFromParams(Map<String, String[]> params) {
        String[] classes = params.get("class");
        if (classes.length != 1) {
            throw new InvalidPluginConfigurationException("there was no or multiple class parameter/s given");
        }
        return classes[0];
    }

    private String getConfigClassFileName(BlackListProvider availableBlackListProvider) {
        Class configClass = availableBlackListProvider.getConfigClass();
        PropertySource propertySource = (PropertySource) configClass.getAnnotation(PropertySource.class);
        //todo this is not nice
        return propertySource.value()[0].replace("file:", "");
    }

    @Override
    public void putResource(Authentication auth, String resource, Map<String, String[]> params, String body) {
        if (authorityChecker.hasAuthority(auth, getReadWriteAuthorityName()) == false) {
            throw new AccessDeniedException("no access rights to access this plugin");
        }
        if ("blacklistproviderconfig".equals(resource)) {
            String providerClassFromParams = getProviderClassFromParams(params);
            BlackListProvider blackListProvider = getBlackListProviderInstanceByClass(providerClassFromParams);
            Object config = this.gson.fromJson(body, this.activeblackListProvider.getConfigClass());
            blackListProvider.setConfig(config);

            String configString = yamlConfigStringConverter.convertToYAMLString(blackListProvider.getConfig(), blackListProvider.getConfigClass());
            configString = this.configPrefixPatcher.patchConfig(blackListProvider.getConfigClass(), configString);

            String configClassFileName = getConfigClassFileName(blackListProvider);
            try {
                Files.write(new File(configClassFileName).toPath(), configString.getBytes("UTF-8"));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            super.putResource(auth, resource, params, body);
        }
    }
}
