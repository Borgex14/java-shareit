package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto);
    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);
    BookingResponseDto getBooking(Long userId, Long bookingId);
    List<BookingResponseDto> getUserBookings(Long userId, BookingStatus Status, Integer from, Integer size);
    List<BookingResponseDto> getOwnerBookings(Long userId, BookingStatus Status, Integer from, Integer size);
}