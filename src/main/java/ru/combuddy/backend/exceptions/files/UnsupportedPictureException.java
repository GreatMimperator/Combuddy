package ru.combuddy.backend.exceptions.files;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unsupported picture. Check available extensions list")
public class UnsupportedPictureException extends RuntimeException {
    public UnsupportedPictureException(String message) {
        super(message);
    }
}
