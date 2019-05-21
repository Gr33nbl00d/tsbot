package de.greenblood.tsbot.plugins.apexmatchmaker;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import de.greenblood.tsbot.common.Ts3BotContext;
import de.greenblood.tsbot.common.TsApiUtils;
import de.greenblood.tsbot.TsBotPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Greenblood on 14.04.2019.
 */
public class ApexMatchMakerTsBotPlugin implements TsBotPlugin {

  private TsApiUtils tsApiUtils = new TsApiUtils();

  @Override
  public void onClientJoin(Ts3BotContext context, ClientJoinEvent e) {
  }

  @Override
  public void onTextMessage(Ts3BotContext context, TextMessageEvent e) {
    if (tsApiUtils.isPrivateMessageForBot(e, context.getSessionInfo()) && e.getMessage().contains("!playapex")) {
      Pattern pattern = Pattern.compile("\\!playapex (.*)");
      Matcher matcher = pattern.matcher(e.getMessage());
      if (matcher.matches()) {
        //originPlayerFinder.findPlayer(matcher.group(1));
      }
    }
  }

  @Override
  public void init(Ts3BotContext context) {

  }

  @Override
  public void onClientMoved(Ts3BotContext context, ClientMovedEvent e) {

  }
}
