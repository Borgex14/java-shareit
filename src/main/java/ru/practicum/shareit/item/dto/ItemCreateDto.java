package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemCreateDto {
    private String name;
    private String description;
    private boolean available;
    private String owner;
    private String request;
}