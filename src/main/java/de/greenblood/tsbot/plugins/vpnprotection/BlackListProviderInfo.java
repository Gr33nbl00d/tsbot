package de.greenblood.tsbot.plugins.vpnprotection;

public class BlackListProviderInfo {
    private String providerName;
    private String className;

    public BlackListProviderInfo() {
    }

    public BlackListProviderInfo(String providerName, String className) {
        this.providerName = providerName;
        this.className = className;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getClassName() {
        return className;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
