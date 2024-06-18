package net.univwork.api.api_v1.exception;

public class NoRepeatException extends RuntimeException {
    public NoRepeatException(String message) {
        super(message);
    }
}
