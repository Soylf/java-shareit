package ru.practicum.shareit.booking.server;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingRequestDto bookingRequestDto);

    BookingDto updateStatus(long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(long userId, Long bookingId);

    List<BookingDto> getAllUser(long userId, String state, int from, int size);

    List<BookingDto> getAllOwner(long userId, String state, int from, int size);
}
