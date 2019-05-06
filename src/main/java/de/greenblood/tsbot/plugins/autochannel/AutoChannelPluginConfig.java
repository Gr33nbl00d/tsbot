package de.greenblood.tsbot.plugins.autochannel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("autochannelplugin")
public class AutoChannelPluginConfig {
    private List<AutoChannelConfig> autoChannelList;

    public List<AutoChannelConfig> getAutoChannelList() {
        return autoChannelList;
    }

    public void setAutoChannelList(List<AutoChannelConfig> autoChannelList) {
        this.autoChannelList = autoChannelList;
    }
}
