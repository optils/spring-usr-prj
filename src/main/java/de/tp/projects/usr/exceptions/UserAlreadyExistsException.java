package de.tp.projects.usr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsException extends ResponseStatusException {

    public UserAlreadyExistsException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }

}
