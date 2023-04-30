package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleIncorrectValidate(DuplicateException e) {
        log.error("Что-то пошло не так! Возможно, такой объект уже существует.");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundedObjects(NotFoundException e) {
        log.error("Что-то пошло не так! Объект не найден.");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleNotFoundedObjects(ValidationException e) {
        log.error("Что-то пошло не так! Проверьте, корректно ли введены данные.");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundUserForItemException.class)
    public ResponseEntity<?> handleNotFoundedObjects(NotFoundUserForItemException e) {
        log.error("Что-то пошло не так! Редактировать информацию о товаре может только его владелец.");
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
