package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component("userMemoryStorage")
public class InMemoryUserStorage implements UserStorage {

    private long userId = 1;
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя = {}", user);
        emailCheck(user);
        long id = generateId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Обновление пользователя с id = {}", id);
        emailCheck(user);
        if (!users.containsKey(id)) {
            log.error("Обновляемый пользователь с id = {} не существует", id);
            throw new NotFoundException("Обновляемый пользователь не существует");
        }
        User existingUser = users.get(id);
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        users.put(id, existingUser);

        return existingUser;
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя с id = {}", id);
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        log.info("Получение всех пользователей");
        return List.copyOf(users.values());
    }

    @Override
    public User findById(Long id) {
        log.info("Получение пользователя с id = {}", id);
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не существует");
        }

        return user;
    }

    private synchronized long generateId() {
        return userId++;
    }

    private void emailCheck(User user) {
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail()) && !existingUser.getId().equals(user.getId())) {
                log.error("Пользователь с указанным email = {} уже существует", user.getEmail());
                throw new ConflictException("Пользователь с указанным email существует");
            }
        }
    }
}