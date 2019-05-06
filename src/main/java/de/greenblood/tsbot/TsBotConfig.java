package de.greenblood.tsbot;

import com.github.theholywaffle.teamspeak3.TS3Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("botconfig")
public class TsBotConfig {
    private String tsUsername;
    private String tsPassword;
    private String botNickname;
    private List<String> tsBotPluginList;
    private String botHomeChannel;

    public String getTsUsername() {
        return tsUsername;
    }

    public String getTsPassword() {
        return tsPassword;
    }

    public String getBotNickname() {
        return botNickname;
    }

    public List<String> getTsBotPluginList() {
        return tsBotPluginList;
    }

    public void setTsUsername(String tsUsername) {
        this.tsUsername = tsUsername;
    }

    public void setTsPassword(String tsPassword) {
        this.tsPassword = tsPassword;
    }

    public void setBotNickname(String botNickname) {
        this.botNickname = botNickname;
    }

    public void setTsBotPluginList(List<String> tsBotPluginList) {
        this.tsBotPluginList = tsBotPluginList;
    }

    public String getBotHomeChannel() {
        return botHomeChannel;
    }

    public void setBotHomeChannel(String botHomeChannel) {
        this.botHomeChannel = botHomeChannel;
    }
}
