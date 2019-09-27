package de.greenblood.tsbot.plugins.topg;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.greenblood.tsbot.caches.ClientsOnlineRetriever;
import de.greenblood.tsbot.common.*;
import de.greenblood.tsbot.restservice.AuthorityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
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
    private Ts3BotContext context;

    @Override
    public void reloadPlugin(Ts3BotContext context) {

    }

    @Override
    public String getReadWriteAuthorityName() {
        return "topg_maintainer";
    }

    @Override
    public UpdateablePluginConfig<TopgPluginConfig> getConfig() {
        return topgPluginConfig;
    }

    @Override
    public void postResource(Ts3BotContext context, Authentication auth, String resource, Map<String, String[]> params) {
        log.info("Post request from pubg " + resource + " " + convertWithStream(params));
    }

    @Override
    public Object getResource(Ts3BotContext context, Authentication auth, String resource, Map<String, String[]> params) {
        // Get request from pubg topgvoting {p_resp=[testuser], ip=[5.79.90.39]}
        log.info("Get request from pubg " + resource + " " + convertWithStream(params));
        List<Client> clients = ClientsOnlineRetriever.getInstance().getClients(context, 0);
        String ipFromParams = getIpFromParams(params);
        for (Client client : clients) {
            if (ipFromParams.equals(client.getIp())) {
                String message = new MessageFormatingBuilder()
                        .addClient(client)
                        .build(this.topgPluginConfig.getVoteMessage());
                context.getAsyncApi().sendTextMessage(TextMessageTargetMode.CLIENT, client.getId(), message);
                context.getAsyncApi().addClientToServerGroup(topgPluginConfig.getServerGroupToAssignOnVote(), client.getDatabaseId());
                //22685
                //"[COLOR=gray][B] | [/B][COLOR=orangered][B]Phoenix[/B][/COLOR][/COLOR] Â» [b] Vielen Dank fÃ¼r dein Vote. Du kannst in 24 Stunden erneut Voten und dir einen Boost abholen!"
            }
        }

        return null;
    }

    private String getIpFromParams(Map<String, String[]> params) {
        String ip = null;
        String[] p_resps = params.get("p_resp");
        String[] ips = params.get("ip");
        if (p_resps != null && p_resps.length == 1) {
            String p_resp = p_resps[0];
            String[] split = p_resp.split("zz");
            if (split.length == 2) {
                ip = split[1].replace("y", ".");
            }
        }

        if (ip == null && ips != null && ips.length == 1) {
            ip = ips[0];
        }
        return ip;
    }

    @Override
    public void init(Ts3BotContext context) {
        this.context = context;
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
