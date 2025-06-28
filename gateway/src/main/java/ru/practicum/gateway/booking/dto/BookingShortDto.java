package ru.practicum.gateway.booking.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingShortDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}