package ru.combuddy.backend.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Not enough authority")
public class NotEnoughAuthorityException extends RuntimeException {
    public NotEnoughAuthorityException(String message) {
        super(message);
    }
}
