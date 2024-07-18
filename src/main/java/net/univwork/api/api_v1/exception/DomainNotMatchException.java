package net.univwork.api.api_v1.exception;

public class DomainNotMatchException extends RuntimeException {
    public DomainNotMatchException(String message) {
        super(message);
    }
}
