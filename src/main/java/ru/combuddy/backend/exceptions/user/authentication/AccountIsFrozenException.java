package ru.combuddy.backend.exceptions.user.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "This account is frozen")
public class AccountIsFrozenException extends RuntimeException {
    public AccountIsFrozenException(String message) {
        super(message);
    }
}
