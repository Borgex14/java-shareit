package ru.practicum.shareit.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(Long itemId) {
        super("Item with id " + itemId + " is not available for booking");
    }
}
