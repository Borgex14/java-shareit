package ru.practicum.gateway.Item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.gateway.request.ItemRequest;
import ru.practicum.gateway.user.User;

@Data
public class ItemCreateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private User owner;
    private ItemRequest request;
}