package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemCreateDto createDto) {
        return ResponseEntity.ok(itemService.addItem(userId, createDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemCreateDto updateDtos) {
        try {
            return ResponseEntity.ok(itemService.updateItem(userId, itemId, updateDtos));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        try {
            return ResponseEntity.ok(itemService.getItem(itemId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.getItemsByOwnerId(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        List<ItemDto> results = itemService.searchItems(text);
        return ResponseEntity.ok(results);
    }
}