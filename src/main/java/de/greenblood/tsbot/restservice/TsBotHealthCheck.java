package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.Ts3Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class TsBotHealthCheck implements HealthIndicator {

    @Autowired
    Ts3Bot ts3Bot;

    @Override
    public Health health() {
        if (ts3Bot.isConnected()) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("teamspek.connection","down").build();
        }
    }
}
