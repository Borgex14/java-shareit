package ru.practicum.gateway.exception;

public class AccessError extends RuntimeException {
    public AccessError(String message) {
        super(message);
    }
}
