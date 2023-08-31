package ru.combuddy.backend.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Invalid role name")
public class InvalidRoleNameException extends RuntimeException {
    public InvalidRoleNameException(String message) {
        super(message);
    }
}
