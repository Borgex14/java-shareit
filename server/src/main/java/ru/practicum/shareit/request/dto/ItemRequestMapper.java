package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest fromCreateDto(CreateItemRequestDto dto, User requester) {
        if (dto == null || requester == null) return null;
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestorId(requester.getId())
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest entity, List<ItemRequestCreateDto> items) {
        Objects.requireNonNull(entity, "Entity must not be null");
        if (entity.getId() == null) {
            throw new IllegalStateException("Entity must be persisted before mapping");
        }
        return ItemRequestDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .requestorId(entity.getRequestorId())
                .created(entity.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest entity) {
        return toDto(entity, null);
    }

    public static void updateFromDto(ItemRequest entity, CreateItemRequestDto dto) {
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}