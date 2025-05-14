package ru.practicum.shareit.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

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
}