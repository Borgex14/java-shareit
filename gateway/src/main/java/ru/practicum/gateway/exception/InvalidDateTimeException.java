package ru.practicum.gateway.exception;

public class InvalidDateTimeException extends IllegalArgumentException {
    public InvalidDateTimeException(String message) {
        super(message);
    }
}
