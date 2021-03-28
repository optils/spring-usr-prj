package de.tp.projects.usr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IdMissmatchException extends ResponseStatusException {

    public IdMissmatchException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

}
