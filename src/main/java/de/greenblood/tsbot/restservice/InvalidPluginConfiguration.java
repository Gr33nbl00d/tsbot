package de.greenblood.tsbot.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Created by Greenblood on 24.05.2019.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPluginConfiguration extends RuntimeException {
    public InvalidPluginConfiguration(String message) {
        super(message);
    }

    public InvalidPluginConfiguration(String message, Throwable e) {
        super(message, e);
    }
}
