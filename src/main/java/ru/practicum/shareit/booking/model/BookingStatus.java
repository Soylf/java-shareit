package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    static BookingStatus from(String state) {
        for (BookingStatus value: BookingStatus.values()) {
            if(value.name().equals(state)) {
                return value;
            }
        }
        return null;
    } 
}
