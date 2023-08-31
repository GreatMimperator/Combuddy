package ru.combuddy.backend.exceptions.permission.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Not permitted requested state")
public class NotPermittedPostStateException extends RuntimeException {
    public NotPermittedPostStateException(String message) {
        super(message);
    }
}
