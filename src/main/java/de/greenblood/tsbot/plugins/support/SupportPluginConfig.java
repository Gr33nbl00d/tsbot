package de.greenblood.tsbot.plugins.support;

import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import de.greenblood.tsbot.common.UpdateablePluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix="supportplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:support.yml")
@Validated
public class SupportPluginConfig implements UpdateablePluginConfig<SupportPluginConfig> {

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

  @Override
  public void update(SupportPluginConfig supportPluginConfig) {
    this.registrationChannelConfig = supportPluginConfig.registrationChannelConfig;
    this.supportChannels=supportPluginConfig.supportChannels;
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
    private String configName;
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

    public void setConfigName(String configName) {
      this.configName = configName;
    }

    public String getConfigName() {
      return configName;
    }
  }
}
