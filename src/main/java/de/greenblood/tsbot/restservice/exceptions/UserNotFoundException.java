package de.greenblood.tsbot.restservice.exceptions;

/**
 * Created by Greenblood on 24.05.2019.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
