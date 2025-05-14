package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long idCounter = 1L; // для генерации ID

    @Override
    public synchronized UserDto createUser(UserCreateDto createDto) {
        Long id = idCounter++;
        User user = new User();
        user.setId(String.valueOf(id));
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        users.put(id, user);
        return UserMapper.toDto(user);
    }

    @Override
    public synchronized UserDto updateUser(Long userId, UserCreateDto updateDto) {
        User existing = users.get(userId);
        if (existing == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        if (updateDto.getName() != null) {
            existing.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null) {
            existing.setEmail(updateDto.getEmail());
        }
        return UserMapper.toDto(existing);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }
}