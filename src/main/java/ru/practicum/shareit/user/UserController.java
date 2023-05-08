package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Добавляем пользователя " + userDto.getName() + "...");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDtoResponse> readUser(@PathVariable("id") @Min(1) Long userId) {
        log.debug("Получаем инфо о пользователе с id " + userId + "...");
        return ResponseEntity.status(HttpStatus.OK).body(userService.readUser(userId));
    }

    @GetMapping
    public ResponseEntity<UserListDto> readUsers() {
        log.debug("Получаем инфо обо всех пользователях приложения...");
        return ResponseEntity.status(HttpStatus.OK).body(userService.readUsers());
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserDtoResponse> editUser(@RequestBody UserDtoUpdate userDtoUpdate,
                                                    @PathVariable("id") Long userId) {
        log.debug("Редактируем инфо о пользователе с id " + userId + "...");
        return ResponseEntity.status(HttpStatus.OK).body(userService.editUser(userDtoUpdate, userId));
    }

    @DeleteMapping("{id}")
    public void deleteUser(@Min(1) @PathVariable("id") Long userId) {
        log.debug("Удаляем пользователя с id " + userId + "...");
        userService.deleteUser(userId);
    }
}