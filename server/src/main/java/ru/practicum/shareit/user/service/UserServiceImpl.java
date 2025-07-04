package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMappingUtils;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserMappingUtils userMappingUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserMappingUtils userMappingUtils) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userMappingUtils = userMappingUtils;
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto createDto) {
        User user = userMapper.fromCreateDto(createDto);
        User createdUser = userRepository.save(user);
        return userMappingUtils.toDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserCreateDto updateDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (updateDto.getName() != null) {
            existingUser.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(updateDto.getEmail(), userId)) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
            existingUser.setEmail(updateDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMappingUtils.toDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return userMappingUtils.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMappingUtils::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }
}