package ru.combuddy.backend.exceptions.general;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Page number is not in permitted range")
public class IllegalPageNumberException extends RuntimeException {
    public IllegalPageNumberException(String message) {
        super(message);
    }
}
