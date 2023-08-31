package ru.combuddy.backend.exceptions.contact;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Contact not found")
public class NotFoundUserContactException extends RuntimeException {
    public NotFoundUserContactException(String message) {
        super(message);
    }
}
