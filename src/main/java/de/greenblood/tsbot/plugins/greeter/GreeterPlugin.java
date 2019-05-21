package de.greenblood.tsbot.plugins.greeter;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.greenblood.tsbot.caches.ClientInfoRetriever;
import de.greenblood.tsbot.common.DefaultTsBotPlugin;
import de.greenblood.tsbot.common.MessageFormattingUtil;
import de.greenblood.tsbot.common.Ts3BotContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class GreeterPlugin extends DefaultTsBotPlugin {

  @Autowired
  private GreeterPluginConfig greeterPluginConfig;
  private final MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    TS3ApiAsync asyncApi = context.getAsyncApi();
    List<String> greetingMessages = greeterPluginConfig.getGreetingMessages();
    ClientInfo clientInfo = ClientInfoRetriever.getInstance().retrieve(context, e.getClientId(), true);
    for (String greetingMessage : greetingMessages) {
      asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), messageFormattingUtil.format(greetingMessage, clientInfo));
    }
  }

}
