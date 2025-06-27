package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemRequestDto {
    @NotBlank(message = "Описание обязательно")
    @Size(min = 1, max = 512, message = "Длина описания должна быть от 1 до 512 символов")
    private String description;
}
