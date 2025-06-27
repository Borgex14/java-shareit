package ru.practicum.shareit.exception;

public class AccessError extends RuntimeException {
    public AccessError(String message) {
        super(message);
    }
}
