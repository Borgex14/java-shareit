package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    private String id;
    private String name;
    private String description;
    private boolean available;
    private String owner;
    private String request;
}