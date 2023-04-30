package ru.practicum.shareit.handler;

public class NotFoundUserForItemException extends RuntimeException {
    public NotFoundUserForItemException(String message) {
        super(message);
    }
}
