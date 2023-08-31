package ru.combuddy.backend.exceptions.permission.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Post content update not permitted")
public class PostContentUpdateNotPermittedException extends RuntimeException {
    public PostContentUpdateNotPermittedException(String message) {
        super(message);
    }
}
