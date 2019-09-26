package de.greenblood.tsbot.plugins.usercounter;

import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import de.greenblood.tsbot.common.UpdateablePluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix="usercounterplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:usercounter.yml")
@Validated
public class UserCounterPluginConfig implements UpdateablePluginConfig<UserCounterPluginConfig> {

    @NotNull
    private List<UserCounterConfig> userCounterList;

    public List<UserCounterConfig> getUserCounterList() {
        return userCounterList;
    }

    public void setUserCounterList(List<UserCounterConfig> userCounterList) {
        this.userCounterList = userCounterList;
    }

    @Override
    public void update(UserCounterPluginConfig userCounterPluginConfig) {
        this.userCounterList = userCounterPluginConfig.userCounterList;
    }

}
