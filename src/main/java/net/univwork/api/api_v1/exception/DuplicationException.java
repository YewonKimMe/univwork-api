package net.univwork.api.api_v1.exception;

public class DuplicationException extends RuntimeException {
    public DuplicationException(String message) {
        super(message);
    }
}
