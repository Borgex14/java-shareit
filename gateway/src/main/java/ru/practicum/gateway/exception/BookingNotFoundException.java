package ru.practicum.gateway.exception;

public class BookingNotFoundException extends IllegalArgumentException {
    public BookingNotFoundException(Long id) {
        super("Booking with id " + id + " not found");
    }
}