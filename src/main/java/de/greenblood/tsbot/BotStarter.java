package de.greenblood.tsbot;

import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class BotStarter implements CommandLineRunner, WebMvcConfigurer {

    @Autowired
    private Ts3Bot ts3Bot;

    private final static Logger log = LoggerFactory.getLogger(BotStarter.class);

    public static void main(String[] args) {

        SpringApplication.run(BotStarter.class, args);

    }

    @Override
    public void run(String... strings) throws Exception {

        List<GrantedAuthority> permissions = new ArrayList();
        permissions.add(new SimpleGrantedAuthority("user_maintainer"));


        boolean connected = false;
        while (connected == false) {
            try {
                this.ts3Bot.connect();
                connected = true;
                return;
            } catch (TS3ConnectionFailedException e) {
                log.warn("Error while connecting to teamspeak server. Retrying...", e);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                String msg = "interrupted while sleeping";
                log.info(msg);
                log.debug(msg, e);
                return;
            }

        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("file:www/")
                .setCachePeriod(0);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // forward requests to /admin and /user to their index.html
        registry.addViewController("/").setViewName(
                "forward:/index.html");
    }
}
