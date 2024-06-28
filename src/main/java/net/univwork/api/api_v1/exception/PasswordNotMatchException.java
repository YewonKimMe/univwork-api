package net.univwork.api.api_v1.exception;

public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
