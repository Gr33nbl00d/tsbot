package de.greenblood.tsbot.plugins.autochannel;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Greenblood on 03.05.2019.
 */
@Component
public class AutoChannelConfig {
    private String channelName;
    private String newChannelName;
    private String command;
    private List<String> autoChannelHelloMessages;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getNewChannelName() {
        return newChannelName;
    }

    public void setNewChannelName(String newChannelName) {
        this.newChannelName = newChannelName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getAutoChannelHelloMessages() {
        return autoChannelHelloMessages;
    }

    public void setAutoChannelHelloMessages(List<String> autoChannelHelloMessages) {
        this.autoChannelHelloMessages = autoChannelHelloMessages;
    }
}
