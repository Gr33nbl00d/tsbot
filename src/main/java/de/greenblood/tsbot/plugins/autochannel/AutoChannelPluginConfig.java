package de.greenblood.tsbot.plugins.autochannel;

import de.greenblood.tsbot.common.UpdatableTsBotPlugin;
import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("autochannelplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:autochannel.yml")
public class AutoChannelPluginConfig implements UpdateablePluginConfig<AutoChannelPluginConfig>{

  private List<AutoChannelConfig> autoChannelList;

  public List<AutoChannelConfig> getAutoChannelList() {
    return autoChannelList;
  }

  public void setAutoChannelList(List<AutoChannelConfig> autoChannelList) {
    this.autoChannelList = autoChannelList;
  }

  @Override
  public void update(AutoChannelPluginConfig autoChannelPluginConfig) {
    this.autoChannelList = autoChannelPluginConfig.getAutoChannelList();
  }
}
