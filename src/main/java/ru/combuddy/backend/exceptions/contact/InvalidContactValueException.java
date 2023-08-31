package ru.combuddy.backend.exceptions.contact;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid contact value")
public class InvalidContactValueException extends RuntimeException {
    public InvalidContactValueException(String message) {
        super(message);
    }
}
