package ru.combuddy.backend.exceptions.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Illegal post state")
public class IllegalPostStateException extends RuntimeException {
    public IllegalPostStateException(String message) {
        super(message);
    }
}
