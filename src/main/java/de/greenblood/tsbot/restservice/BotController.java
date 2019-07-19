package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.Ts3Bot;
import de.greenblood.tsbot.common.TsBotPlugin;
import de.greenblood.tsbot.common.UpdatableTsBotPlugin;
import de.greenblood.tsbot.database.*;
import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;
import de.greenblood.tsbot.restservice.security.SpringBootSecurityManager;
import de.greenblood.tsbot.restservice.security.UserDetailsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Component
public class BotController {
    private static final Logger log = LoggerFactory.getLogger(BotController.class);
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    Ts3Bot ts3Bot;
    @Autowired
    DefaultUserCreater defaultUserCreater;
    @Autowired
    UserManager userManager;
    @Autowired
    SpringBootSecurityManager springBootSecurityManager;

    @PostConstruct
    public void postConstruct() {

        if (isFirstBotStart()) {
            defaultUserCreater.createDefaultUser();
        }
    }

    private boolean isFirstBotStart() {
        Path path = Paths.get("init.dat");
        if (Files.exists(path))
            return false;
        else {
            try {

                Files.write(path, Collections.singleton("1"), Charset.forName("US-ASCII"));
                return true;
            } catch (IOException e) {
                log.error("error writing init.dat", e);
                return true;
            }
        }
    }

    @RequestMapping(path = "/odysseus/api/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Iterable<Users> getUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isUserMaintainer(auth)) {
            return userManager.getAllUsers();
        } else {
            throw new AccessDeniedException("access denied");
        }
    }

    private boolean isUserMaintainer(Authentication auth) {
        return hasAuthority(auth, "user_maintainer");
    }

    private boolean hasAuthority(Authentication auth, String authorityString) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(authorityString)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    @RequestMapping(path = "/odysseus/api/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> addUser(@RequestBody Users user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isUserMaintainer(auth)) {
            userManager.saveUser(user);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @Transactional
    @RequestMapping(path = "/odysseus/api/users/{username}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> changeUserRights(@PathVariable("username") String userName, @RequestBody List<Authorities> newAuthorities) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isUserMaintainer(auth)) {

            userManager.updateUserRights(userName, newAuthorities);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Transactional
    @RequestMapping(path = "/odysseus/api/users/{username}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(@PathVariable("username") String userName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isUserMaintainer(auth)) {
            if (userManager.doesUserExist(userName)) {
                userManager.deleteUser(userName);
            } else {
                throw new UserNotFoundException("user " + userName + " does not exist");
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Transactional
    @RequestMapping(path = "/odysseus/api/users", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> deleteUser(@RequestBody Users user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isUserMaintainer(auth)) {
            userManager.deleteUser(user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(path = "/odysseus/api/pluginconfig/{configName}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> readConfig(@PathVariable String configName) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UpdatableTsBotPlugin pluginByConfigName = getUpdateablePluginByConfigName(configName);
        if (hasAuthority(auth, pluginByConfigName.getReadWriteAuthorityName())) {
            try {

                //todo new method to only check if the name is valid not retuning a plugin
                if (pluginByConfigName != null) {
                    //Todo this is not nice
                    String configFileName = getFileNameFromPlugin(pluginByConfigName);
                    Path path = new File(configFileName).toPath();
                    byte[] bytes = Files.readAllBytes(path);
                    String config = new String(bytes, Charset.forName("UTF-8"));
                    config = stripFirstLineAndOneIdentationLevel(config);
                    return new ResponseEntity<String>(config, HttpStatus.OK);
                } else {
                    throw new UnknownPluginException("Plugin with name " + configName + " was not found");
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private String stripFirstLineAndOneIdentationLevel(String config) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(config));
        int lineNumber = 0;
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            if (lineNumber >= 1) {
                stringBuffer.append(line.substring(2) + "\n");
            }
            lineNumber++;
        }
        return stringBuffer.toString();
    }


    @RequestMapping(path = "/odysseus/api/pluginconfig/{configName}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> updateConfig(@PathVariable String configName, @RequestBody String configString) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UpdatableTsBotPlugin plugin = getUpdateablePluginByConfigName(configName);

        if (hasAuthority(auth, plugin.getReadWriteAuthorityName())) {
            UpdateablePluginConfig config = (UpdateablePluginConfig) autowireCapableBeanFactory.getBean(plugin.getConfigClass());

            Yaml yaml = new Yaml(new Constructor(plugin.getConfigClass()));
            try {
                Object load = yaml.load(configString);
                if (load.getClass().equals(plugin.getConfigClass()) == false) {
                    throw new InvalidPluginConfiguration("Plugin configuration is of wrong type");
                }
                config.update(load);
                ts3Bot.reloadPlugin(plugin);
                ConfigurationProperties configurationProperties = (ConfigurationProperties) plugin.getConfigClass().getAnnotation(ConfigurationProperties.class);
                String configRootName = configurationProperties.value();
                StringBuffer buffer = new StringBuffer();
                buffer.append(configRootName + ":\n");
                BufferedReader reader = new BufferedReader(new StringReader(configString));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append("  " + line + "\n");
                }
                Path configFile = new File("./" + getFileNameFromPlugin(plugin)).toPath();
                Files.write(configFile, buffer.toString().getBytes(Charset.forName("UTF-8")));
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (YAMLException e) {
                throw new InvalidPluginConfiguration("Error parsing configuration: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new InvalidPluginConfiguration("Error parsing configuration", e);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private UpdatableTsBotPlugin getUpdateablePluginByConfigName(String configName) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        ListableBeanFactory listableBeanFactory = (ListableBeanFactory) autowireCapableBeanFactory;
        Map<String, Object> beansWithAnnotation = listableBeanFactory.getBeansWithAnnotation(TsBotPlugin.class);
        for (Object o : beansWithAnnotation.values()) {
            if (o instanceof UpdatableTsBotPlugin) {
                UpdatableTsBotPlugin plugin = (UpdatableTsBotPlugin) o;
                String fileName = getFileNameFromPlugin(plugin);
                //todo this is not nice
                if (fileName.toLowerCase().startsWith(configName.toLowerCase()))
                    return plugin;
            }
        }
        throw new UnknownPluginException("Plugin with name " + configName + " was not found");
    }

    @RequestMapping(path = "/odysseus/api/loggedinuser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    Users getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return null;
        return ((UserDetailsAdapter) auth.getPrincipal()).getUserObject();
    }


    @RequestMapping(path = "/odysseus/api/roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    List<String> getAllAuthorities() {
        return userManager.getAllAuthorities();
    }


    @RequestMapping(path = "/odysseus/api/logout", method = RequestMethod.POST)
    public ResponseEntity login(@Autowired HttpServletResponse response, @Autowired HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/odysseus/api/login", method = RequestMethod.POST)
    public ResponseEntity login(@Autowired HttpServletRequest request, @RequestBody LoginInfo loginInfo) {

        try {
            AuthenticationManager authenticationManager = springBootSecurityManager.authenticationManagerBean();
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword());
            token.setDetails(new WebAuthenticationDetails(request));//if request is needed during authentication
            Authentication auth;
            try {
                auth = authenticationManager.authenticate(token);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(auth);
                //if user has a http session you need to save context in session for subsequent requests
                HttpSession session = request.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
                return ResponseEntity.status(HttpStatus.OK).build();

            } catch (AuthenticationException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    private String getFileNameFromPlugin(UpdatableTsBotPlugin plugin) {
        Class configClass = plugin.getConfigClass();
        PropertySource propertySource = (PropertySource) plugin.getConfigClass().getAnnotation(PropertySource.class);
        //todo this is not nice
        return propertySource.value()[0].replace("file:", "");
    }

}