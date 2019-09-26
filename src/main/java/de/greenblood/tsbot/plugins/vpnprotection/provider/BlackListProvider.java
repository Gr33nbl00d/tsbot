package de.greenblood.tsbot.plugins.vpnprotection.provider;

public interface BlackListProvider {

    BlackListCheckResult isBlacklistedIp(String ip);

    String getProviderName();

    Class getConfigClass();

    Object getConfig();

    void setConfig(Object config);
}
