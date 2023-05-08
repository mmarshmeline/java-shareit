package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.handler.exception.StateException;
import ru.practicum.shareit.handler.response.StateErrorResponse;


@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    private ResponseEntity<String> handleException(ResponseStatusException e) {
        log.error("Что-то пошло не так! Ошибка " + e.getStatus());
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<String> handleException(MethodArgumentNotValidException e) {
        log.error("Что-то пошло не так! Ошибка " + HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST + e.getFieldError().getDefaultMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<String> handleException(MethodArgumentTypeMismatchException e) {
        log.error("Что-то пошло не так! Ошибка " + HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST + "Некорректные параметры строки " + e.getName()
                + " = " + e.getValue(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<String> handleException() {
        log.error("Что-то пошло не так! Ошибка " + HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR + " Нарушение уникального индекса или " +
                "первичного ключа", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StateException.class)
    private ResponseEntity<StateErrorResponse> handleException(StateException e) {
        log.error("Что-то пошло не так! Ошибка " + HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new StateErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}