package de.greenblood.tsbot.plugins.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties("supportplugin")
@Validated
public class SupportPluginConfig {

  private RegistrationChannelConfig registrationChannelConfig;
  private List<SupportChannelConfig> supportChannels;

  public RegistrationChannelConfig getRegistrationChannelConfig() {
    return registrationChannelConfig;
  }

  public void setRegistrationChannelConfig(RegistrationChannelConfig registrationChannelConfig) {
    this.registrationChannelConfig = registrationChannelConfig;
  }

  public List<SupportChannelConfig> getSupportChannels() {
    return supportChannels;
  }

  public void setSupportChannels(List<SupportChannelConfig> supportChannels) {
    this.supportChannels = supportChannels;
  }

  public static class RegistrationChannelConfig extends SupportChannelConfig {

    private int newUserGroup;

    public int getNewUserGroup() {
      return newUserGroup;
    }

    public void setNewUserGroup(int newUserGroup) {
      this.newUserGroup = newUserGroup;
    }
  }

  public static class SupportChannelConfig {

    @NotNull
    private String channelSearchString;
    private List<Integer> serverGroupsToInform;
    @NotNull
    private String greetingMessage;
    @NotNull
    private String noSuporterOnlineMessage;
    @NotNull
    private String supporterMessage;
    private List<Integer> serverGroupsToIgnore;

    public String getNoSuporterOnlineMessage() {
      return noSuporterOnlineMessage;
    }

    public void setNoSuporterOnlineMessage(String noSuporterOnlineMessage) {
      this.noSuporterOnlineMessage = noSuporterOnlineMessage;
    }

    public List<Integer> getServerGroupsToIgnore() {
      return serverGroupsToIgnore;
    }

    public void setServerGroupsToIgnore(List<Integer> serverGroupsToIgnore) {
      this.serverGroupsToIgnore = serverGroupsToIgnore;
    }

    public String getChannelSearchString() {
      return channelSearchString;
    }

    public void setChannelSearchString(String channelSearchString) {
      this.channelSearchString = channelSearchString;
    }

    public List<Integer> getServerGroupsToInform() {
      return serverGroupsToInform;
    }

    public void setServerGroupsToInform(List<Integer> serverGroupsToInform) {
      this.serverGroupsToInform = serverGroupsToInform;
    }

    public String getGreetingMessage() {
      return greetingMessage;
    }

    public void setGreetingMessage(String greetingMessage) {
      this.greetingMessage = greetingMessage;
    }

    public String getSupporterMessage() {
      return supporterMessage;
    }

    public void setSupporterMessage(String supporterMessage) {
      this.supporterMessage = supporterMessage;
    }
  }
}
