package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class MessageFormattingUtil
{
    public String format(String message, Client client)
    {
        message = message.replaceAll("\\%IP\\%", client.getIp());
        message = message.replaceAll("\\%NICKNAME\\%", client.getNickname());
        return message;
    }

    public String formatCommand(String message, String commandName)
    {
        return message.replaceAll("\\%COMMAND\\%", commandName);
    }

    public String formatNewChannel(String message, TextMessageEvent e)
    {
        return message.replaceAll("\\%NICKNAME\\%", e.getInvokerName());
    }
}
