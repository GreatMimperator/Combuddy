package ru.combuddy.backend.exceptions.contact;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User contact already exists")
public class UserContactAlreadyExistsException extends RuntimeException {
    public UserContactAlreadyExistsException(String message) {
        super(message);
    }
}
