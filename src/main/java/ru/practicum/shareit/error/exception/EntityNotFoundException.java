package ru.practicum.shareit.error.exception;

public class EntityNotFoundException extends IllegalStateException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}