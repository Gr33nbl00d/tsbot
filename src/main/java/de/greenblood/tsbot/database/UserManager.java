package de.greenblood.tsbot.database;

import de.greenblood.tsbot.common.PluginManager;
import de.greenblood.tsbot.common.TsBotPlugin;
import de.greenblood.tsbot.common.UpdatableTsBotPlugin;
import de.greenblood.tsbot.restservice.UnknownAuthorityException;
import de.greenblood.tsbot.restservice.UserAlreadyExistsException;
import de.greenblood.tsbot.restservice.UserNotFoundException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserManager {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    AuthoritiesRepository authoritiesRepository;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    PluginManager pluginManager;

    public void saveUser(@RequestBody Users user) {
        if (usersRepository.findById(user.getUsername()).isPresent() == false) {
            usersRepository.save(user);

            List<Authorities> authorities = user.getAuthorities();
            for (Authorities authority : authorities) {
                authority.setUsername(user.getUsername());
            }
            authoritiesRepository.saveAll(authorities);
        } else {
            throw new UserAlreadyExistsException("user " + user.getUsername() + " does already exist");
        }
    }

    public Iterable<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public boolean doesUserExist(String userName) {
        return usersRepository.findById(userName).isPresent();
    }

    public void deleteUser(String userName) {
        authoritiesRepository.deleteAllFromUserName(userName);
        if (usersRepository.findById(userName).isPresent()) {
            usersRepository.deleteById(userName);
        } else {
            throw new UserNotFoundException("user " + userName + " not found");
        }
    }

    public Users getUserByUsername(String username) {
        return usersRepository.findById(username).orElseThrow(() -> new UserNotFoundException("user " + username + " not found"));
    }

    public void updateUserRights(String userName, List<Authorities> newAuthorities) {
        List<String> allAuthorities = getAllAuthorities();
        for (Authorities authority : newAuthorities) {
            if (allAuthorities.contains(authority.getAuthority()) == false) {
                throw new UnknownAuthorityException("authority unknown: " + authority.getAuthority());
            }
            authority.setUsername(userName);
        }

        authoritiesRepository.deleteAllFromUserName(userName);
        authoritiesRepository.saveAll(newAuthorities);

    }

    public List<String> getAllAuthorities() {
        ArrayList<String> allRoles = new ArrayList<>();
        allRoles.add("user_maintainer");
        List<UpdatableTsBotPlugin> allActivePlugins = pluginManager.getUpdatableTsBotPluginList();
        for (UpdatableTsBotPlugin allActivePlugin : allActivePlugins) {
            allRoles.add(allActivePlugin.getReadWriteAuthorityName());
        }
        return allRoles;
    }
}
