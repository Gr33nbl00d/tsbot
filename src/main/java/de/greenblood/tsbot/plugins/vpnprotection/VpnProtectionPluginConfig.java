package de.greenblood.tsbot.plugins.vpnprotection;

import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import java.util.List;

@Component
@ConfigurationProperties("vpnprotectionplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:vpnprotection.yml")
@Validated
public class VpnProtectionPluginConfig implements UpdateablePluginConfig<VpnProtectionPluginConfig> {

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

  @Override
  public void update(VpnProtectionPluginConfig vpnProtectionPluginConfig) {
    this.blackListProvider=vpnProtectionPluginConfig.blackListProvider;
    this.ipCacheSize=vpnProtectionPluginConfig.ipCacheSize;
    this.kickMessage=vpnProtectionPluginConfig.kickMessage;
    this.whiteList=vpnProtectionPluginConfig.whiteList;
  }
}
