package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

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
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        log.info("Добавлен новый пользователь.");
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> readUsers() {
        log.info("Возвращаем список пользователей.");
        return new ResponseEntity<>(userService.readUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> readUser(@PathVariable Long userId) {
        log.info("Возвращаем информацию о пользователе с id " + userId + ".");
        return new ResponseEntity<>(userService.readUser(userId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> editUser(@PathVariable Long userId, @RequestBody User user) {
        log.info("Информация о пользователе с id " + userId + " отредактирована.");
        return new ResponseEntity<>(userService.editUser(userId, user), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long userId) {
        log.info("Пользователь с id " + userId + " удален.");
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
    }
}
