package net.univwork.api.api_v1.exception;

public class NoAuthenticationException extends RuntimeException {
    public NoAuthenticationException(String message) {
        super(message);
    }
}
