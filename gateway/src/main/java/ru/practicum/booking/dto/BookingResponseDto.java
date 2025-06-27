package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private ItemDto item;
    private UserDto booker;

    @Data
    @Builder
    public static class Booker {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class Item {
        private Long id;
        private String name;
    }
}