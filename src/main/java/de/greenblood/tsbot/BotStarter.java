package de.greenblood.tsbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BotStarter implements CommandLineRunner {

    @Autowired
    private Ts3Bot ts3Bot;

    public static void main(String[] args) {

        SpringApplication.run(BotStarter.class, args);

    }

    @Override
    public void run(String... strings) throws Exception {
        this.ts3Bot.connect();
    }
}
