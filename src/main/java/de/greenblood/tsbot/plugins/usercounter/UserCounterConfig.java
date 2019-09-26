package de.greenblood.tsbot.plugins.usercounter;

import javax.validation.constraints.NotNull;
import java.util.List;

public class UserCounterConfig {

    @NotNull
    private String configName;
    @NotNull
    private String channelSearchString;
    private List<Integer> serverGroupsToIgnore;
    private List<Integer> serverGroupsToCount;
    @NotNull
    private String channelNameTemplate;
    private UserCounterOnlineRecordConfig onlineRecordConfig;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public UserCounterOnlineRecordConfig getOnlineRecordConfig() {
      return onlineRecordConfig;
    }

    public void setOnlineRecordConfig(
        UserCounterOnlineRecordConfig onlineRecordConfig) {
      this.onlineRecordConfig = onlineRecordConfig;
    }

    public String getChannelSearchString() {
      return channelSearchString;
    }

    public void setChannelSearchString(String channelSearchString) {
      this.channelSearchString = channelSearchString;
    }

    public List<Integer> getServerGroupsToIgnore() {
      return serverGroupsToIgnore;
    }

    public void setServerGroupsToIgnore(List<Integer> serverGroupsToIgnore) {
      this.serverGroupsToIgnore = serverGroupsToIgnore;
    }

    public List<Integer> getServerGroupsToCount() {
      return serverGroupsToCount;
    }

    public void setServerGroupsToCount(List<Integer> serverGroupsToCount) {
      this.serverGroupsToCount = serverGroupsToCount;
    }

    public void setChannelNameTemplate(String channelNameTemplate) {
      this.channelNameTemplate = channelNameTemplate;
    }

    public String getChannelNameTemplate() {
      return this.channelNameTemplate;
    }

    public static class UserCounterOnlineRecordConfig {

      private String onlineRecordId;
      private String channelSearchString;
      @NotNull
      private String channelNameTemplate;

      public String getOnlineRecordId() {
        return onlineRecordId;
      }

      public void setOnlineRecordId(String onlineRecordId) {
        this.onlineRecordId = onlineRecordId;
      }

      public String getChannelSearchString() {
        return channelSearchString;
      }

      public void setChannelSearchString(String channelSearchString) {
        this.channelSearchString = channelSearchString;
      }

      public String getChannelNameTemplate() {
        return channelNameTemplate;
      }

      public void setChannelNameTemplate(String channelNameTemplate) {
        this.channelNameTemplate = channelNameTemplate;
      }
    }
  }
