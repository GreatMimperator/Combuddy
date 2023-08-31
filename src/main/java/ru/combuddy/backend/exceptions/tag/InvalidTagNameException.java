package ru.combuddy.backend.exceptions.tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Invalid tag")
public class InvalidTagNameException extends RuntimeException {
    public InvalidTagNameException(String message) {
        super(message);
    }
}
