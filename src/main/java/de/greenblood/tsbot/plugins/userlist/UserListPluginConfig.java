package de.greenblood.tsbot.plugins.userlist;

import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties("userlistplugin")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:userlist.yml")
@Validated
public class UserListPluginConfig implements UpdateablePluginConfig<UserListPluginConfig> {

    @NotNull
    @NotEmpty
    private List<UserListConfig> userListConfigList;

    public List<UserListConfig> getUserListConfigList() {
        return userListConfigList;
    }

    public void setUserListConfigList(List<UserListConfig> userListConfigList) {
        this.userListConfigList = userListConfigList;
    }

    @Override
    public void update(UserListPluginConfig userListPluginConfig) {
        this.userListConfigList = userListPluginConfig.userListConfigList;
    }


    public static class UserListConfig {

        @NotNull
        private String channelSearchString;
        @NotNull
        private String templateFileLocation;
        @NotNull
        @NotEmpty
        private List<UserList> userList;
        @NotNull
        private String offlineHtmlTemplate;
        @NotNull
        private String onlineHtmlTemplate;

        public String getOnlineHtmlTemplate() {
            return onlineHtmlTemplate;
        }

        public void setOnlineHtmlTemplate(String onlineHtmlTemplate) {
            this.onlineHtmlTemplate = onlineHtmlTemplate;
        }

        public void setOfflineHtmlTemplate(String offlineHtmlTemplate) {
            this.offlineHtmlTemplate = offlineHtmlTemplate;
        }

        public List<UserList> getUserList() {
            return userList;
        }

        public void setUserList(List<UserList> userList) {
            this.userList = userList;
        }

        public String getChannelSearchString() {
            return channelSearchString;
        }

        public void setChannelSearchString(String channelSearchString) {
            this.channelSearchString = channelSearchString;
        }

        public String getTemplateFileLocation() {
            return templateFileLocation;
        }

        public void setTemplateFileLocation(String templateFileLocation) {
            this.templateFileLocation = templateFileLocation;
        }

        public String getOfflineHtmlTemplate() {
            return offlineHtmlTemplate;
        }
    }

    public static class UserList {

        @NotNull
        private String identifier;
        @NotNull
        private String entryHtmlTemplate;
        @NotNull
        @NotEmpty
        private List<Integer> serverGroupsToInclude;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getEntryHtmlTemplate() {
            return entryHtmlTemplate;
        }

        public void setEntryHtmlTemplate(String entryHtmlTemplate) {
            this.entryHtmlTemplate = entryHtmlTemplate;
        }

        public List<Integer> getServerGroupsToInclude() {
            return serverGroupsToInclude;
        }

        public void setServerGroupsToInclude(List<Integer> serverGroupsToInclude) {
            this.serverGroupsToInclude = serverGroupsToInclude;
        }
    }
}
