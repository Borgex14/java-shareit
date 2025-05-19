package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @NotNull(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;
}