package de.greenblood.tsbot.common;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

import java.util.ArrayList;
import java.util.List;

public class TsApiUtils {
    public boolean isNotMessageFromMyself(TextMessageEvent e, ServerQueryInfo clientInfo) {
        return isMessageFromMyself(e, clientInfo) == false;
    }

    private boolean isMessageFromMyself(TextMessageEvent e, ServerQueryInfo clientInfo) {
        if (e.getInvokerId() == clientInfo.getId()) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isNewUser(ClientJoinEvent e,int newUserGroup) {
        List<Integer> groups = parseGroups(e.getClientServerGroups());
        if (groups.size() == 1 && groups.get(0).equals(newUserGroup)) {
            return true;
        } else {
            return false;
        }
    }

    private List<Integer> parseGroups(String groupsString) {
        String[] groupArray = groupsString.split(",");
        List<Integer> groupIdList = new ArrayList<Integer>();
        for (String groupIdStr : groupArray) {
            groupIdList.add(Integer.parseInt(groupIdStr));
        }
        return groupIdList;

    }

    public void informAllOnlineMembersAboutNewUser(final TS3ApiAsync asyncApi, final String clientNickname, final List<Integer> serverGroupsToInform, String informText) {
        asyncApi.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
            @Override
            public void handleSuccess(List<Client> result) {
                int count=0;
                for (Client client : result) {
                    for (Integer serverGroup : serverGroupsToInform) {
                        if (client.isInServerGroup(serverGroup)) {
                            asyncApi.sendTextMessage(TextMessageTargetMode.CLIENT, client.getId(), String.format(informText,clientNickname));
                            count++;
                            break;
                        }
                    }
                }
                //asyncApi.sendTextMessage("i informed "+ count +" supporter");


            }
        });
    }

    public boolean isPrivateMessageForBot(TextMessageEvent e, ServerQueryInfo clientInfo) {
        return (e.getTargetMode() == TextMessageTargetMode.CLIENT) && (e.getTargetClientId() == clientInfo.getId());
    }

}
