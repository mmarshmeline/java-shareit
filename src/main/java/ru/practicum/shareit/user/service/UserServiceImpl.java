package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository users, UserMapper mapper) {
        this.userRepository = users;
        this.userMapper = mapper;
    }

    @Override
    public UserDtoResponse createUser(UserDto userDto) {
        log.debug("Добавляем пользователя " + userDto.getName() + "...");
        return userMapper.mapToUserDtoResponse(userRepository.save(userMapper.mapToUserFromUserDto(userDto)));
    }

    @Override
    public UserDtoResponse readUser(Long userId) {
        log.debug("Получаем инфо о пользователе с id " + userId + "...");
        return userMapper.mapToUserDtoResponse(userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении нет."))
        );
    }

    @Override
    public UserListDto readUsers() {
        log.debug("Получаем инфо обо всех пользователях приложения...");
        return UserListDto.builder()
                .users(userRepository.findAll().stream().map(userMapper::mapToUserDtoResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public UserDtoResponse editUser(UserDtoUpdate userDtoUpdate, Long userId) {
        User updatingUser = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя в приложении нет."));
        log.debug("Редактируем инфо о пользователе с id " + userId + "...");
        return userMapper.mapToUserDtoResponse(userRepository.save(userMapper.mapToUserFromUserDtoUpdate(userDtoUpdate, updatingUser)));
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Удаляем пользователя с id " + userId + "...");
        userRepository.deleteById(userId);
    }
}