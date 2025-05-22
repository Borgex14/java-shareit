package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;

    private Item findItemById(Long itemId) throws NotFoundException {
        Item item = itemStorage.getItem(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id " + itemId + " не найдена");
        }
        return item;
    }

    @Override
    public synchronized ItemDto addItem(Long userId, ItemCreateDto createDto) {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь не найден");
        }
        Item item = ItemMapper.fromCreateDto(createDto);
        item.setOwner(user);
        Item savedItem = itemStorage.addItem(userId, item);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public synchronized ItemDto updateItem(Long ownerId, Long itemId, ItemCreateDto updateDto) {
        Item existingItem = findItemById(itemId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на изменение этой вещи");
        }

        boolean isUpdated = false;

        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            existingItem.setName(updateDto.getName());
            isUpdated = true;
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            existingItem.setDescription(updateDto.getDescription());
            isUpdated = true;
        }

        if (updateDto.getAvailable() != null) {
            existingItem.setAvailable(updateDto.getAvailable());
            isUpdated = true;
        }

        itemStorage.updateItem(ownerId, itemId, existingItem);

        if (!isUpdated) {
            throw new IllegalArgumentException("Нет полей для обновления");
        }

        return ItemMapper.toDto(existingItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = findItemById(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        List<Item> items = itemStorage.getItemsByOwnerId(ownerId);
        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();

        List<Item> itemsFromStorage = itemStorage.searchItems(text);

        return itemsFromStorage.stream()
                .filter(i -> i.getAvailable() &&
                        (i.getName() != null && i.getName().toLowerCase().contains(lowerText) ||
                                i.getDescription() != null && i.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}