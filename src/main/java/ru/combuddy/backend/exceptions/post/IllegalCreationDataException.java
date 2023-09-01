package ru.combuddy.backend.exceptions.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Illegal creation data")
public class IllegalCreationDataException extends RuntimeException {
    public IllegalCreationDataException(String message) {
        super(message);
    }
}
