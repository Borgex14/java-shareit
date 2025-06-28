package ru.practicum.Item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.request.ItemRequest;
import ru.practicum.user.User;

@Data
public class ItemCreateDto {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;
    private Long requestId;
    private User owner;
    private ItemRequest request;
}