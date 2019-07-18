package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.database.Authorities;
import de.greenblood.tsbot.database.UserManager;
import de.greenblood.tsbot.database.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Component
public class DefaultUserCreater {
    private static final String DEFAULT_USER = "odysseus";
    private static final Logger log = LoggerFactory.getLogger(BotController.class);
    @Autowired
    RandomPasswordGenerator randomPasswordGenerator;
    @Autowired
    UserManager userManager;

    public void createDefaultUser() {
        log.info("generating initial random password for default user odysseus");
        Path destination = Paths.get("initialpassword.txt");
        try {
            String randomPassword = this.randomPasswordGenerator.generatePassword(10);
            Files.write(destination, Collections.singleton(randomPassword), Charset.forName("US-ASCII"));
            Authorities o = new Authorities();
            o.setUsername(DEFAULT_USER);
            o.setAuthority("user_maintainer");
            Users user = new Users();
            user.setAuthorities(Collections.singletonList(o));
            user.setUsername(DEFAULT_USER);

            user.setPassword(new BCryptPasswordEncoder().encode(randomPassword));
            userManager.saveUser(user);

        } catch (IOException e) {
            log.error("error writing initial password to file" + destination, e);

        }
    }
}
