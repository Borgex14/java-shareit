package ru.practicum.gateway.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    @NotNull(message = "Start time cannot be null")
    @FutureOrPresent(message = "Start time must be in present or future")
    private LocalDateTime start;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be in future")
    private LocalDateTime end;
}