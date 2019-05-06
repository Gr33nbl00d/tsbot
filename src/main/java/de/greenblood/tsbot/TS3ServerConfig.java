package de.greenblood.tsbot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Greenblood on 29.04.2019.
 */
@Component
@ConfigurationProperties("serverconfig")
public class TS3ServerConfig {
    private int queryPort;
    private int commandTimeout;
    private boolean enableCommunicationsLogging;
    private String host;
    private int virtualServerIdentifier;
    private boolean selectVirtualServerByPort;

    public void setQueryPort(int queryPort) {
        this.queryPort = queryPort;
    }

    public void setCommandTimeout(int commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public void setEnableCommunicationsLogging(boolean enableCommunicationsLogging) {
        this.enableCommunicationsLogging = enableCommunicationsLogging;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getQueryPort() {
        return queryPort;
    }

    public int getCommandTimeout() {
        return commandTimeout;
    }

    public boolean isEnableCommunicationsLogging() {
        return enableCommunicationsLogging;
    }

    public String getHost() {
        return host;
    }

    public int getVirtualServerIdentifier() {
        return virtualServerIdentifier;
    }

    public void setVirtualServerIdentifier(int virtualServerIdentifier) {
        this.virtualServerIdentifier = virtualServerIdentifier;
    }

    public boolean isSelectVirtualServerByPort() {
        return selectVirtualServerByPort;
    }

    public void setSelectVirtualServerByPort(boolean selectVirtualServerByPort) {
        this.selectVirtualServerByPort = selectVirtualServerByPort;
    }
}
