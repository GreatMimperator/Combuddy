package ru.combuddy.backend.exceptions.user.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User authentication has failed")
public class UserAuthenticationException extends RuntimeException {
    public UserAuthenticationException(String message, AuthenticationException reason) {
        super(message, reason);
    }
}
