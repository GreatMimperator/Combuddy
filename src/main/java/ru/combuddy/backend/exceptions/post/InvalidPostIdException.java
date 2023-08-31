package ru.combuddy.backend.exceptions.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Post with this id does not exist")
public class InvalidPostIdException extends RuntimeException {
    public InvalidPostIdException(String message) {
        super(message);
    }
}
