package de.greenblood.tsbot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("botconfig")
public class TsBotConfig {

    private String loginName;
    private String loginPassword;
    private String botNickname;
    private List<String> tsBotPluginList;
    private String botHomeChannel;

    public String getLoginName() {
        return loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public String getBotNickname() {
        return botNickname;
    }

    public List<String> getTsBotPluginList() {
        return tsBotPluginList;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
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
