package de.greenblood.tsbot.plugins.topg;

import de.greenblood.tsbot.BotStarter;
import de.greenblood.tsbot.common.*;
import de.greenblood.tsbot.restservice.AuthorityChecker;
import de.greenblood.tsbot.restservice.exceptions.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class TopgPlugin extends UpdatableTsBotPlugin<TopgPluginConfig> {

    @Autowired
    private TopgPluginConfig topgPluginConfig;
    @Autowired
    private AuthorityChecker authorityChecker;
    private final static Logger log = LoggerFactory.getLogger(TopgPlugin.class);

    @Override
    public void reloadPlugin(Ts3BotContext context) {

    }

    @Override
    public String getReadWriteAuthorityName() {
        return null;
    }

    @Override
    public UpdateablePluginConfig<TopgPluginConfig> getConfig() {
        return topgPluginConfig;
    }

    @Override
    public void postResource(Authentication auth, String resource, Map<String, String[]> params) {
        log.info("Post request from pubg " + resource + " " + convertWithStream(params));
    }

    @Override
    public Object getResource(Authentication auth, String resource, Map<String, String[]> params) {
        // Get request from pubg topgvoting {p_resp=[testuser], ip=[5.79.90.39]}
        log.info("Get request from pubg " + resource + " " + convertWithStream(params));
        return null;
    }

    public String convertWithStream(Map<String, String[]> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + Arrays.toString(map.get(key)))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

    @Override
    public Class<TopgPluginConfig> getConfigClass() {
        return TopgPluginConfig.class;
    }
}
