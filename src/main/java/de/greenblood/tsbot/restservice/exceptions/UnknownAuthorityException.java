package de.greenblood.tsbot.restservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownAuthorityException extends RuntimeException {
    public UnknownAuthorityException(String message) {
        super(message);
    }
}
