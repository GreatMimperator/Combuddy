package ru.combuddy.backend.exceptions;

public class RegexValidationException extends IllegalStateException {
    public RegexValidationException() {
    }

    public RegexValidationException(String message) {
        super(message);
    }

    public RegexValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegexValidationException(Throwable cause) {
        super(cause);
    }
}
