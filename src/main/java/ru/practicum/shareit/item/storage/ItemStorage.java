package ru.practicum.shareit.item.storage;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(@NotNull Long owner, Item item);

    Item updateItem(Long ownerId, Long itemId, Item updateItem);

    Item getItem(Long itemId);

    List<Item> getItemsByOwnerId(Long ownerId);

    List<Item> searchItems(String text);
}