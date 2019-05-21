package de.greenblood.tsbot.plugins.greeter;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.greenblood.tsbot.TsBotPlugin;
import de.greenblood.tsbot.common.MessageFormattingUtil;
import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.caches.ClientInfoRetriever;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
public class GreeterPlugin implements TsBotPlugin {

  @Autowired
  private GreeterPluginConfig greeterPluginConfig;
  private final MessageFormattingUtil messageFormattingUtil = new MessageFormattingUtil();

  public GreeterPlugin() {
  }

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    TS3ApiAsync asyncApi = context.getAsyncApi();
    List<String> greetingMessages = greeterPluginConfig.getGreetingMessages();
    ClientInfo clientInfo = ClientInfoRetriever.getInstance().retrieve(context, e.getClientId(), true);
    for (String greetingMessage : greetingMessages) {
      asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), messageFormattingUtil.format(greetingMessage, clientInfo));
    }
  }

  @Override
  public void onTextMessage(Ts3BotContext context, TextMessageEvent e) {

  }

  @Override
  public void init(Ts3BotContext context) {

  }

  @Override
  public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {

  }

}
