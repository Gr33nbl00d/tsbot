package de.greenblood.tsbot.plugins.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("supportplugin")
public class SupportPluginConfig {
    private String registrationChannelName;
    private int newUserGroup;
    private List<Integer> serverGroupsToInform;
    private String newUserGreetingText;
    private String supporterMessageText;

    public String getRegistrationChannelName() {
        return registrationChannelName;
    }

    public void setRegistrationChannelName(String registrationChannelName) {
        this.registrationChannelName = registrationChannelName;
    }

    public int getNewUserGroup() {
        return newUserGroup;
    }

    public void setNewUserGroup(int newUserGroup) {
        this.newUserGroup = newUserGroup;
    }

    public List<Integer> getServerGroupsToInform() {
        return serverGroupsToInform;
    }

    public void setServerGroupsToInform(List<Integer> serverGroupsToInform) {
        this.serverGroupsToInform = serverGroupsToInform;
    }

    public String getNewUserGreetingText() {
        return newUserGreetingText;
    }

    public void setNewUserGreetingText(String newUserGreetingText) {
        this.newUserGreetingText = newUserGreetingText;
    }

    public String getSupporterMessageText() {
        return supporterMessageText;
    }

    public void setSupporterMessageText(String supporterMessageText) {
        this.supporterMessageText = supporterMessageText;
    }
}
