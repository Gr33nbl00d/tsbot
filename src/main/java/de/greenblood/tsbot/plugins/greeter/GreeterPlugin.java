package de.greenblood.tsbot.plugins.greeter;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.greenblood.tsbot.caches.ClientInfoRetriever;
import de.greenblood.tsbot.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Greenblood on 14.04.2019.
 */
@Component
@TsBotPlugin
public class GreeterPlugin extends UpdatableTsBotPlugin<GreeterPluginConfig> {

  @Autowired
  private GreeterPluginConfig greeterPluginConfig;

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
    TS3ApiAsync asyncApi = context.getAsyncApi();
    List<String> greetingMessages = greeterPluginConfig.getGreetingMessages();
    ClientInfo clientInfo = ClientInfoRetriever.getInstance().retrieve(context, e.getClientId(), true);
    for (String greetingMessage : greetingMessages) {
      String message = new MessageFormatingBuilder()
          .addClient(clientInfo)
          .build(greetingMessage);
      asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, e.getClientId(), message);
    }
  }

  @Override
  public Class<GreeterPluginConfig> getConfigClass() {
    return GreeterPluginConfig.class;
  }

  @Override
  public void reloadPlugin(Ts3BotContext context) {

  }

  @Override
  public UpdateablePluginConfig<GreeterPluginConfig> getConfig(){
    return this.greeterPluginConfig;
  }

  @Override
  public String getReadWriteAuthorityName() {
    return "greeter_maintainer";
  }
}
