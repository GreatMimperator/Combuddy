package ru.combuddy.backend.exceptions.files;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE, reason = "The file weight is not supported")
public class FIleWeightException extends RuntimeException {
    public FIleWeightException(String message) {
        super(message);
    }
}
