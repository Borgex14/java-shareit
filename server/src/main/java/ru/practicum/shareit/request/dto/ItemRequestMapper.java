package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest fromCreateDto(CreateItemRequestDto dto, User requestor) {
        Objects.requireNonNull(requestor, "Requestor cannot be null");
        if (dto == null) return null;
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest request, List<ItemRequestCreateDto> items) {
        if (request == null) return null;

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestor() != null ?
                        request.getRequestor().getId() : null)
                .created(request.getCreated())
                .items(items != null ? items : Collections.emptyList())
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