package ru.combuddy.backend.exceptions.user.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Refresh token has compromised. Log in again")
public class CompromisedRefreshTokenException extends RuntimeException {
    public CompromisedRefreshTokenException(String message) {
        super(message);
    }
}
