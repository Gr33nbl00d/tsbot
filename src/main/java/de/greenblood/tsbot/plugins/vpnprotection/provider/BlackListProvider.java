package de.greenblood.tsbot.plugins.vpnprotection.provider;

public interface BlackListProvider {

  BlackListCheckResult isBlacklistedIp(String ip);
}
