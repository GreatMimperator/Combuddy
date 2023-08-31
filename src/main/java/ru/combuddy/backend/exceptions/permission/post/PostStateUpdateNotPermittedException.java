package ru.combuddy.backend.exceptions.permission.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Post state update not permitted")
public class PostStateUpdateNotPermittedException extends RuntimeException {
    public PostStateUpdateNotPermittedException(String message) {
        super(message);
    }
}
