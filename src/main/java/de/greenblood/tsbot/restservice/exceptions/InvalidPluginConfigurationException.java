package de.greenblood.tsbot.restservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Greenblood on 24.05.2019.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPluginConfigurationException extends RuntimeException {
    public InvalidPluginConfigurationException(String message) {
        super(message);
    }

    public InvalidPluginConfigurationException(String message, Throwable e) {
        super(message, e);
    }
}
