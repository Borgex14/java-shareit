package ru.practicum.shareit.exception;

public class InvalidDateTimeException extends IllegalArgumentException {
    public InvalidDateTimeException(String message) {
        super(message);
    }
}
