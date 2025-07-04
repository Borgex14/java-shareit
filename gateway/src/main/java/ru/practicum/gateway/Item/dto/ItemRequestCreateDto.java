package ru.practicum.gateway.Item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreateDto {

    private long id;

    @NotBlank
    private String name;

    private Long ownerId;
}
