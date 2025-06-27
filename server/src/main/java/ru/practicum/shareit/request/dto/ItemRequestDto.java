package ru.practicum.shareit.request.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestCreateDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
    private long requestorId;
    private LocalDateTime created;

    private List<ItemRequestCreateDto> items;
}