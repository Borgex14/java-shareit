package ru.practicum.shareit.exception;

public class BookingNotFoundException extends IllegalArgumentException {
    public BookingNotFoundException(Long id) {
        super("Booking with id " + id + " not found");
    }
}