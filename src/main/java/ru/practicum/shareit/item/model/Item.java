package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Item {
    private String id;
    private String name;
    private String description;
    private boolean available;
    private String owner;
    private String request;
}
