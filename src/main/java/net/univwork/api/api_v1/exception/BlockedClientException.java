package net.univwork.api.api_v1.exception;

public class BlockedClientException extends RuntimeException {
    public BlockedClientException(String message) {
        super(message);
    }
}
