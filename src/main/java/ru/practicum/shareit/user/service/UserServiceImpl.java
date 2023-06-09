package ru.practicum.shareit.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(User user) {
        log.debug("Добавляем пользователя " + user.getName() + "...");
        return userRepository.createUser(user);
    }

    @Override
    public List<UserDto> readUsers() {
        return userRepository.readUsers();
    }

    @Override
    public UserDto editUser(Long userId, User user) {
        log.debug("Редактируем товар " + user.getName() + "...");
        return userRepository.editUser(userId, user);
    }

    @Override
    public UserDto deleteUser(Long userId) {
        log.debug("Удаляем пользователя с id " + userId + "...");
        return userRepository.deleteUser(userId);
    }

    @Override
    public UserDto readUser(Long userId) {
        log.debug("Получаем информацию о пользователе с id " + userId + "...");
        return userRepository.readUser(userId);
    }
}
