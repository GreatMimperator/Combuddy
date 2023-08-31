package ru.combuddy.backend.exceptions.contact;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Invalid contact type")
public class InvalidContactTypeException extends RuntimeException {
    public InvalidContactTypeException(String message) {
        super(message);
    }
}
