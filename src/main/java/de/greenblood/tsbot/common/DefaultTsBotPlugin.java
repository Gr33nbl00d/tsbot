package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import de.greenblood.tsbot.restservice.exceptions.PluginWebRequestNotSupported;

import java.util.Map;

public abstract class DefaultTsBotPlugin<T> implements TsBotPluginInterface<T>{

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
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

  @Override
  public void onClientLeave(Ts3BotContext context, ClientLeaveEvent e) {
  }

  @Override
  public abstract Class<T> getConfigClass();

}
