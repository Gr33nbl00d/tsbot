package de.greenblood.tsbot.plugins.vpnprotection;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import java.util.List;

@Component
@ConfigurationProperties("vpnprotectionplugin")
@Validated
public class VpnProtectionPluginConfig {

  @NotNull
  private String blackListProvider;
  @NotNull
  private Integer ipCacheSize;
  @NotNull
  private List<String> whiteList;
  @NotNull
  private String kickMessage;

  public String getKickMessage() {
    return kickMessage;
  }

  public void setKickMessage(String kickMessage) {
    this.kickMessage = kickMessage;
  }

  public String getBlackListProvider() {
    return blackListProvider;
  }

  public void setBlackListProvider(String blackListProvider) {
    this.blackListProvider = blackListProvider;
  }

  public Integer getIpCacheSize() {
    return ipCacheSize;
  }

  public void setIpCacheSize(Integer ipCacheSize) {
    this.ipCacheSize = ipCacheSize;
  }

  public List<String> getWhiteList() {
    return whiteList;
  }

  public void setWhiteList(List<String> whiteList) {
    this.whiteList = whiteList;
  }

}
