package de.greenblood.tsbot.plugins.autochannel;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * Created by Greenblood on 03.05.2019.
 */
@Component
@Validated
public class AutoChannelConfig {

  @NotNull
  private String channelName;
  @NotNull
  private boolean autoCreateOnJoin;
  @NotNull
  private String newChannelName;
  @NotNull
  private String command;
  private List<String> autoChannelHelloMessages;
  @NotNull
  private List<String> autoChannelCreatedMessages;

  public List<String> getAutoChannelCreatedMessages() {
    return autoChannelCreatedMessages;
  }

  public void setAutoChannelCreatedMessages(List<String> autoChannelCreatedMessages) {
    this.autoChannelCreatedMessages = autoChannelCreatedMessages;
  }

  public boolean isAutoCreateOnJoin() {
    return autoCreateOnJoin;
  }

  public void setAutoCreateOnJoin(boolean autoCreateOnJoin) {
    this.autoCreateOnJoin = autoCreateOnJoin;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getNewChannelName() {
    return newChannelName;
  }

  public void setNewChannelNameUnlimitedUsers(String newChannelNameUnlimitedUsers) {
    this.newChannelName = newChannelNameUnlimitedUsers;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public List<String> getAutoChannelHelloMessages() {
    return autoChannelHelloMessages;
  }

  public void setAutoChannelHelloMessages(List<String> autoChannelHelloMessages) {
    this.autoChannelHelloMessages = autoChannelHelloMessages;
  }

  public void setNewChannelName(String newChannelName) {
    this.newChannelName = newChannelName;
  }
}
