package de.greenblood.tsbot.plugins.userlist;

import de.greenblood.tsbot.plugins.autochannel.YamlPropertySourceFactory;
import de.greenblood.tsbot.common.UpdateablePluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@ConfigurationProperties(prefix="userlistplugin")
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
        private String configName;
        @NotNull
        private String channelSearchString;
        @NotNull
        @NotEmpty
        private List<UserList> userList;
        @NotNull
        @NotEmpty
        private String offlineHtmlTemplate;
        @NotNull
        @NotEmpty
        private String onlineHtmlTemplate;
        @NotNull
        @NotEmpty
        private String listHtmlTemplate;

        public String getListHtmlTemplate() {
            return listHtmlTemplate;
        }

        public String getConfigName() {
            return configName;
        }

        public void setConfigName(String configName) {
            this.configName = configName;
        }

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

        public void setListHtmlTemplate(String listHtmlTemplate) {
            this.listHtmlTemplate = listHtmlTemplate;
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
