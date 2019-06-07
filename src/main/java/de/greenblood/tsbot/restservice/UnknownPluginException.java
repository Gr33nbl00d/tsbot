package de.greenblood.tsbot.restservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Greenblood on 24.05.2019.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownPluginException extends RuntimeException {
    public UnknownPluginException(String message) {
        super(message);
    }
}
