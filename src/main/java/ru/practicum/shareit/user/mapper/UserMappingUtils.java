package ru.practicum.shareit.user.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMappingUtils {
    private static UserMapper userMapper;

    @Autowired
    public UserMappingUtils(UserMapper userMapper) {
        UserMappingUtils.userMapper = userMapper;
    }

    public static UserDto toDto(User user) {
        return userMapper.toDto(user);
    }

    public static User fromCreateDto(UserCreateDto createDto) {
        return userMapper.fromCreateDto(createDto);
    }
}
