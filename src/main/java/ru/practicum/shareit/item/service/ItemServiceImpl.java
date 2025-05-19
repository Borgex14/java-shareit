package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ru.practicum.shareit.mapper.ItemMapper.toDto;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private UserService userService;
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private long idCounter = 1L;

    @Override
    public synchronized ItemDto addItem(Long userId, ItemCreateDto createDto) {
        User user = UserMapper.toEntity(userService.getUser(userId));
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Long id = idCounter++;
        Item item = new Item();
        item.setId(id);
        item.setName(createDto.getName());
        item.setDescription(createDto.getDescription());
        item.setAvailable(createDto.getAvailable());
        item.setOwner(user);
        items.put(id, item);
        return toDto(item);
    }

    @Override
    public synchronized ItemDto updateItem(Long ownerId, Long itemId, ItemCreateDto updateDto) {
        Item existing = items.get(itemId);
        if (existing == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }
        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на изменение этой вещи");
        }

        boolean isUpdated = false;

        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            existing.setName(updateDto.getName());
            isUpdated = true;
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            existing.setDescription(updateDto.getDescription());
            isUpdated = true;
        }

        if (updateDto.getAvailable() != null) {
            existing.setAvailable(updateDto.getAvailable());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new IllegalArgumentException("Нет полей для обновления");
        }

        return toDto(existing);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }
        return toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwner() != null && i.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getAvailable() &&
                        (i.getName().toLowerCase().contains(lowerText) ||
                                i.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}