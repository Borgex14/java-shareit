package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;


public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }

    public static User fromCreateDto(UserCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        return new User(null, createDto.getName(), createDto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return null;
    }
}