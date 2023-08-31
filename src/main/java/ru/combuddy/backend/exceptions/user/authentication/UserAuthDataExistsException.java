package ru.combuddy.backend.exceptions.user.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "This user auth data already exists")
public class UserAuthDataExistsException extends RuntimeException {
    public UserAuthDataExistsException(String message) {
        super(message);
    }
}
