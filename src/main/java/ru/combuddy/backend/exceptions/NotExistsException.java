package ru.combuddy.backend.exceptions;

public class NotExistsException extends RuntimeException {
    private String who;

    public NotExistsException() {
        super();
    }

    public NotExistsException(String message, String who) {
        super(message);
        this.who = who;
    }

    public NotExistsException(String message, String who, Throwable cause) {
        super(message, cause);
        this.who = who;
    }

    public NotExistsException(Throwable cause, String who) {
        super(cause);
        this.who = who;
    }

    public NotExistsException(String message, String who, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.who = who;
    }

    /**
     * @return who doesn't exist
     */
    public String getWho() {
        return who;
    }
}
