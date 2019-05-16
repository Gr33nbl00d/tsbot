package de.greenblood.tsbot.plugins.vpnprotection.detector;

public interface VpnDetector
{
    BlackListCheckResult isBlacklistedIp(String ip);
}
