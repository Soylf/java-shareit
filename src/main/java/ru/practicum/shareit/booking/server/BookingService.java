package ru.practicum.shareit.booking.server;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exception.EntityNotFoundException;

import java.util.List;

public interface BookingService {


    BookingDto create(long userId, BookingRequestDto bookingRequestDto);

    BookingDto updateStatus(long userId, Long bookingId, boolean approved);

    BookingDto getBooking(long userId, Long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingStatus state);

    List<BookingDto> getUserItemsBooked(long userId, BookingStatus state) throws EntityNotFoundException;
}
