package de.greenblood.tsbot.plugins.topg;

import de.greenblood.tsbot.common.UpdateablePluginConfig;
import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "topgplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:topg.yml")
public class TopgPluginConfig implements UpdateablePluginConfig<TopgPluginConfig> {
    private String voteMessage;
    private int serverGroupToAssignOnVote;

    public String getVoteMessage() {
        return voteMessage;
    }

    public void setVoteMessage(String voteMessage) {
        this.voteMessage = voteMessage;
    }

    @Override
    public void update(TopgPluginConfig topgPluginConfig) {

    }

    public void setServerGroupToAssignOnVote(int serverGroupToAssignOnVote) {
        this.serverGroupToAssignOnVote = serverGroupToAssignOnVote;
    }

    public int getServerGroupToAssignOnVote() {
        return serverGroupToAssignOnVote;
    }
}
