package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserCreateDto createDto);

    UserDto updateUser(Long userId, UserCreateDto updateDto);

    UserDto getUser(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);
}