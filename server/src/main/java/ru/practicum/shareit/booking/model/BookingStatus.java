package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.error.exception.BadRequestException;

public enum BookingStatus {
    ALL,
    APPROVED,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static BookingStatus convert(String state) {
        BookingStatus bookingStatus;
        try {
            bookingStatus = valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Что-то пошло не так");
        }
        return bookingStatus;
    }
}