package ru.practicum.shareit.exception;

public class BookingAlreadyProcessedException extends RuntimeException {
    public BookingAlreadyProcessedException() {
        super("Booking has already been processed");
    }
}
