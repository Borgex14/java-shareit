package ru.practicum.shareit.item.storage;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component("itemMemoryStorage")
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private long generateId() {
        return idGenerator.getAndIncrement();
    }

    @Override
    public Item addItem(@NotNull Long userId, Item item) {
        log.info("Добавление новой вещи: {}", item);
        long id = generateId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, Item updateItem) {
        log.info("Обновление вещи с id = {}", itemId);
        Item existingItem = items.get(itemId);
        if (existingItem == null || !existingItem.getOwner().equals(ownerId)) {
            return null;
        }

        if (updateItem.getName() != null) {
            existingItem.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            existingItem.setDescription(updateItem.getDescription());
        }

        return existingItem;
    }

    @Override
    public Item getItem(Long itemId) {
        log.info("Поиск вещи по id = {}", itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long ownerId) {
        log.info("Поиск вещей владельца с id = {}", ownerId);
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        String lowerText = text.toLowerCase();
        log.info("Поиск вещей по тексту: {}", text);
        return items.values().stream()
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lowerText))
                        || (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }
}