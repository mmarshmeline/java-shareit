package ru.practicum.shareit.user.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public List<UserDto> readUsers() {
        return userRepository.readUsers();
    }

    @Override
    public UserDto editUser(Long userId, User user) {
        return userRepository.editUser(userId, user);
    }

    @Override
    public UserDto deleteUser(Long userId) {
        return userRepository.deleteUser(userId);
    }

    @Override
    public UserDto readUser(Long userId) {
        return userRepository.readUser(userId);
    }
}
