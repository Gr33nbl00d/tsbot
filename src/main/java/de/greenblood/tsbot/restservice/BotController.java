package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.Ts3Bot;
import de.greenblood.tsbot.common.TsBotPlugin;
import de.greenblood.tsbot.common.UpdatableTsBotPlugin;
import de.greenblood.tsbot.database.Users;
import de.greenblood.tsbot.database.UsersRepository;
import de.greenblood.tsbot.plugins.greeter.UpdateablePluginConfig;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@Component
public class BotController {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    Ts3Bot ts3Bot;

    @RequestMapping(path = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public
    @ResponseBody
    Iterable<Users> helloWorld() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
            return usersRepository.findAll();
        } else {
            throw new AccessDeniedException("access denied");
        }
    }

    @RequestMapping(path = "/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> addUser(@RequestBody Users user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
            if (usersRepository.findById(user.getUsername()).isPresent() == false) {
                usersRepository.save(user);
            } else {
                throw new UserAlreadyExistsException("user " + user.getUsername() + " does already exist");
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(path = "/users", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> deleteUser(@RequestBody Users user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
            Users userInDB = usersRepository.findById(user.getUsername()).orElseThrow(() -> new UserNotFoundException("user " + user.getUsername() + " not found"));
            usersRepository.delete(userInDB);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(path = "/pluginconfig/{configName}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> readConfig(@PathVariable String configName) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {

            try {

                UpdatableTsBotPlugin pluginByConfigName = getPluginByConfigName(configName);
                //todo new method to only check if the name is valid not retuning a plugin
                if (pluginByConfigName != null) {
                    Path path = new File(configName).toPath();
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
                stringBuffer.append(line.substring(2) + "\r\n");
            }
            lineNumber++;
        }
        return stringBuffer.toString();
    }


    @RequestMapping(path = "/pluginconfig/{configName}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> updateConfig(@PathVariable String configName, @RequestBody String configString) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {

            UpdatableTsBotPlugin plugin = getPluginByConfigName(configName);

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
                buffer.append(configRootName + ":\r\n");
                BufferedReader reader = new BufferedReader(new StringReader(configString));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append("  " + line + "\n");
                }
                Path configFile = new File("./" + getFileNameFromPlugin(plugin)).toPath();
                Files.write(configFile, buffer.toString().getBytes(Charset.forName("UTF-8")));
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (YAMLException e) {
                throw new InvalidPluginConfiguration("Error parsing configuration", e);
            } catch (IOException e) {
                throw new InvalidPluginConfiguration("Error parsing configuration", e);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private UpdatableTsBotPlugin getPluginByConfigName(String configName) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        ListableBeanFactory listableBeanFactory = (ListableBeanFactory) autowireCapableBeanFactory;
        Map<String, Object> beansWithAnnotation = listableBeanFactory.getBeansWithAnnotation(TsBotPlugin.class);
        for (Object o : beansWithAnnotation.values()) {
            UpdatableTsBotPlugin plugin = (UpdatableTsBotPlugin) o;
            String fileName = getFileNameFromPlugin(plugin);
            if (fileName.equals(configName))
                return plugin;
        }
        throw new UnknownPluginException("Plugin with name " + configName + " was not found");
    }

    private String getFileNameFromPlugin(UpdatableTsBotPlugin plugin) {
        Class configClass = plugin.getConfigClass();
        PropertySource propertySource = (PropertySource) plugin.getConfigClass().getAnnotation(PropertySource.class);
        //todo this is not nice
        return propertySource.value()[0].replace("file:", "");
    }


}