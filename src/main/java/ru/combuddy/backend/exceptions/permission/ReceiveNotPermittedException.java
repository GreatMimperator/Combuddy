package ru.combuddy.backend.exceptions.permission;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Receive not permitted")
public class ReceiveNotPermittedException extends RuntimeException {
    public ReceiveNotPermittedException(String message) {
        super(message);
    }
}
