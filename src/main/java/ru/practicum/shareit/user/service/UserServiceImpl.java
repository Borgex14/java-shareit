package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserCreateDto createDto) {
        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        User createdUser = userStorage.create(user);
        return UserMapper.toDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserCreateDto updateDto) {
        User existingUser = userStorage.findById(userId);

        if (updateDto.getName() != null) {
            existingUser.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null) {
            existingUser.setEmail(updateDto.getEmail());
        }

        User updatedUser = userStorage.updateUser(userId, existingUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userStorage.findById(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.delete(userId);
    }
}