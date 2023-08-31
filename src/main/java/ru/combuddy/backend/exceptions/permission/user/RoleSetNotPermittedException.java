package ru.combuddy.backend.exceptions.permission.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "The role set not permitted")
public class RoleSetNotPermittedException extends RuntimeException {
    public RoleSetNotPermittedException(String message) {
        super(message);
    }
}
