package de.greenblood.tsbot.restservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Greenblood on 24.05.2019.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PluginWebRequestNotSupported extends RuntimeException{
    public PluginWebRequestNotSupported(String message) {
        super(message);
    }

    public PluginWebRequestNotSupported(String message, Throwable cause) {
        super(message, cause);
    }
}
