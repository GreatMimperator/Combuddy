package ru.combuddy.backend.exceptions.permission;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Account freeze state set not permitted")
public class FreezeStateSetNotPermittedException extends RuntimeException {
    public FreezeStateSetNotPermittedException(String message) {
        super(message);
    }
}
