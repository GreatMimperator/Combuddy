package ru.combuddy.backend.exceptions.permission;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Delete not permitted")
public class DeleteNotPermittedException extends RuntimeException {
    public DeleteNotPermittedException(String message) {
        super(message);
    }
}
