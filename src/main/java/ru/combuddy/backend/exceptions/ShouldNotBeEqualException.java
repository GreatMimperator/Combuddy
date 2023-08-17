package ru.combuddy.backend.exceptions;

public class ShouldNotBeEqualException extends IllegalStateException {
    public ShouldNotBeEqualException() {
    }

    public ShouldNotBeEqualException(String message) {
        super(message);
    }

    public ShouldNotBeEqualException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShouldNotBeEqualException(Throwable cause) {
        super(cause);
    }
}
