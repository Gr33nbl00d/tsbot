package de.greenblood.tsbot.plugins.vpnprotection.detector;

public class BlackListCheckResult
{
    private boolean blackListed;

    public BlackListCheckResult(boolean blackListed)
    {
        this.blackListed = blackListed;
    }

    public boolean isBlackListed()
    {
        return blackListed;
    }
}
