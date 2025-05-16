package ru.practicum.shareit.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public static Item toEntity(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.isAvailable(),
                dto.getOwner(),
                dto.getRequest()
        );
    }

    public static Item fromCreateDto(ItemCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        return new Item(
                null,
                createDto.getName(),
                createDto.getDescription(),
                createDto.isAvailable(),
                createDto.getOwner(),
                createDto.getRequest()
        );
    }
}