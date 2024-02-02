package ru.practicum.shareit.error.exception;

public class EntityNotFoundException extends ClassNotFoundException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}