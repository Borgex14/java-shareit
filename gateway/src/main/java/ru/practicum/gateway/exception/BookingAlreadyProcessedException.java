package ru.practicum.gateway.exception;

public class BookingAlreadyProcessedException extends RuntimeException {
    public BookingAlreadyProcessedException() {
        super("Booking has already been processed");
    }
}
