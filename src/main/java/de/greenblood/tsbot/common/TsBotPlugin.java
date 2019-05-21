package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

/**
 * Created by Greenblood on 14.04.2019.
 */
public interface TsBotPlugin {

  void onClientJoin(Ts3BotContext context, ClientJoinEvent e);

  void onTextMessage(Ts3BotContext context, TextMessageEvent e);

  void init(Ts3BotContext context);

  void onClientMoved(Ts3BotContext context, ClientMovedEvent e);

  void onClientLeave(Ts3BotContext context, ClientLeaveEvent e);
}
