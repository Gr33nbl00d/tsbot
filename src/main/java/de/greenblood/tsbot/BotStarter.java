package de.greenblood.tsbot;

import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@SpringBootApplication
public class BotStarter implements CommandLineRunner {

    @Autowired
    private Ts3Bot ts3Bot;

    private final static Logger log = LoggerFactory.getLogger(BotStarter.class);

    //@Autowired
    //EntityRepository entityRepository;

    public static void main(String[] args) {

        SpringApplication.run(BotStarter.class, args);

    }

    @Override
    public void run(String... strings) throws Exception {
        /*
        Entity s = new Entity();
        s.setName("name");
        s.setEmail("mail");
        entityRepository.save(s);

        Entity entity = entityRepository.findById(1L).get();
        System.out.println(entity);
          */
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

    @RestController
    class BotController {
        @Autowired
        UsersRepository usersRepository;


        @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        ResponseEntity<String> helloWorld() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
                return new ResponseEntity<String>(usersRepository.findAll().toString(), HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }
}
