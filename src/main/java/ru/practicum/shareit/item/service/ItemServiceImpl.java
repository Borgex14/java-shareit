package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private long idCounter = 1L; // для генерации ID

    @Override
    public synchronized ItemDto addItem(Long ownerId, ItemCreateDto createDto) {
        Long id = idCounter++;
        Item item = new Item();
        item.setId(String.valueOf(id));
        item.setName(createDto.getName());
        item.setDescription(createDto.getDescription());
        item.setAvailable(createDto.isAvailable());
        item.setOwnerId(String.valueOf(ownerId));
        items.put(id, item);
        return ItemMapper.toDto(item);
    }

    @Override
    public synchronized ItemDto updateItem(Long ownerId, Long itemId, ItemCreateDto updateDto) {
        Item existing = items.get(itemId);
        throw new NoSuchElementException("Вещь не найдена или пользователь не является владельцем");
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return items.values().stream()
                .filter(i -> false)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(i -> i.isAvailable() &&
                        (i.getName().toLowerCase().contains(lowerText) ||
                                i.getDescription().toLowerCase().contains(lowerText)))
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}