package ru.combuddy.backend.exceptions.permission.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Can not apply this action to yourself")
public class SelfActionException extends RuntimeException {
    public SelfActionException(String message) {
        super(message);
    }
}
