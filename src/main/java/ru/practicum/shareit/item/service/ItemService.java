package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userID, ItemCreateDto createDto);
    ItemDto updateItem(Long ownerId, Long itemId, ItemCreateDto updateDto);
    ItemDto getItem(Long itemId);
    List<ItemDto> getItemsByOwnerId(Long ownerId);
    List<ItemDto> searchItems(String text);
}