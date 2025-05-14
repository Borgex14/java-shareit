package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {
    private String id;
    private String name;
    private String description;
    private boolean available;
    private String ownerId;
    private String request;
}
