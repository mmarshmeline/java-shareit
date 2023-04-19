package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    UserDto createUser(User user);

    List<UserDto> readUsers();

    UserDto editUser(Long userId, User user);

    UserDto deleteUser(Long userId);

    UserDto readUser(Long userId);
}
