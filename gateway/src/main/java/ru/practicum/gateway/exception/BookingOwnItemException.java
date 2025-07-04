package ru.practicum.gateway.exception;

public class BookingOwnItemException extends RuntimeException {
    public BookingOwnItemException() {
        super("You cannot book your own item");
    }
}

