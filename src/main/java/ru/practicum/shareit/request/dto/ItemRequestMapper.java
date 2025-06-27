package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    // Создание из DTO
    public static ItemRequest fromCreateDto(CreateItemRequestDto dto, User requester) {
        if (dto == null || requester == null) return null;
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
    }

    // Преобразование в DTO (полное)
    public static ItemRequestDto toDto(ItemRequest entity, List<ItemRequestCreateDto> items) {
        return ItemRequestDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .requestorId(entity.getRequestor().getId())
                .created(entity.getCreated())
                .items(items)
                .build();
    }

    // Преобразование без items
    public static ItemRequestDto toDto(ItemRequest entity) {
        return toDto(entity, null);
    }

    // Обновление сущности
    public static void updateFromDto(ItemRequest entity, CreateItemRequestDto dto) {
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}