package ru.practicum.gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;
import ru.practicum.gateway.Item.dto.ItemDto;
import ru.practicum.gateway.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long id;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    private BookingStatus status;
    private ItemDto item;
    private UserDto booker;
}