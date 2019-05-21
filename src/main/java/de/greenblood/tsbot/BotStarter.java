package de.greenblood.tsbot;

import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotStarter implements CommandLineRunner {

  @Autowired
  private Ts3Bot ts3Bot;

  private final static Logger log = LoggerFactory.getLogger(BotStarter.class);

  @Autowired
  EntityRepository entityRepository;

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
}
