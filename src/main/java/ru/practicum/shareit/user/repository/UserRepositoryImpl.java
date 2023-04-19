package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.handler.DuplicateException;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    private void validateUserEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Что-то пошло не так!");
            throw new ValidationException("E-mail пользователя не может быть пустым!");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Что-то пошло не так!");
            throw new ValidationException("E-mail пользователя должен содержать @");
        }
    }

    private void validateDuplicateEmail(User user) {
        for (User element : users.values()) {
            if (element.getEmail().equals(user.getEmail()) && !Objects.equals(user.getId(), element.getId())) {
                log.warn("Что-то пошло не так!");
                throw new DuplicateException("Пользователь с таким e-mail уже существует!");
            }
        }
    }

    @Override
    public UserDto createUser(User user) {
        validateUserEmail(user);
        validateDuplicateEmail(user);
        user.setId(id);
        users.put(id++, user);
        log.info("Создан пользователь с ID " + user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> readUsers() {
        log.info("Список пользователей приложения: " + users.values());
        return UserMapper.usersToUserDto(users.values());
    }

    @Override
    public UserDto editUser(Long userId, User user) {
        user.setId(userId);
        if (!users.containsKey(userId)) {
            log.warn("Что-то пошло не так!");
            throw new NotFoundException("Такого пользователя нет в приложении!");
        }
        if (user.getName() != null) {
            log.info("Имя пользователя успешно обновлено.");
            users.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {
            validateUserEmail(user);
            validateDuplicateEmail(user);
            log.info("E-mail пользователя " + user.getName() + " успешно обновлен.");
            users.get(userId).setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto deleteUser(Long userId) {
        if (users.containsKey(userId)) {
            User userToDelete = users.get(userId);
            users.remove(userId);
            log.info("Пользователь с ID " + userId + " успешно удален.");
            return UserMapper.toUserDto(userToDelete);
        }
        log.warn("Что-то пошло не так!");
        throw new NotFoundException("Такого пользователя нет в приложении!");
    }

    @Override
    public UserDto readUser(Long userId) {
        if (users.containsKey(userId)) {
            log.info("Получена информация о пользователе с ID " + userId);
            return UserMapper.toUserDto(users.get(userId));
        }
        log.warn("Что-то пошло не так!");
        throw new NotFoundException("Такого пользователя нет в приложении!");
    }
}
