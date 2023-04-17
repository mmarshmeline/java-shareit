package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> handleIncorrectValidate(DuplicateException e) {
        /*return new ResponseEntity<> (e.getMessage(), HttpStatusCode.valueOf(409));*/
        return new ResponseEntity<> (e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundedObjects (NotFoundException e) {
        /*return new ResponseEntity<> (e.getMessage(), HttpStatusCode.valueOf(500));*/
        return new ResponseEntity<> (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleNotFoundedObjects (ValidationException e) {
        /*return new ResponseEntity<> (e.getMessage(), HttpStatusCode.valueOf(400));*/
        return new ResponseEntity<> (e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundUserForItemException.class)
    public ResponseEntity<?> handleNotFoundedObjects (NotFoundUserForItemException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }
}