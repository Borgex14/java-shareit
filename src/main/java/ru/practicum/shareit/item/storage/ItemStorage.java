package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {

    Item addItem(User owner, Item item);
    Item updateItem(Long ownerId, Long itemId, Item updateItem);
    Item getItem(Long itemId);
    List<Item> getItemsByOwnerId(Long ownerId);
    List<Item> searchItems(String text);
}