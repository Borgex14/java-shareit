package ru.practicum.gateway.Item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.gateway.booking.dto.BookingShortDto;
import ru.practicum.gateway.commentDto.CommentDto;
import ru.practicum.gateway.request.ItemRequest;
import ru.practicum.gateway.user.dto.UserDto;

import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull(message = "Available status must be specified")
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}