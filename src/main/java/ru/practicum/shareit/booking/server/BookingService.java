package ru.practicum.shareit.booking.server;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {


    BookingDto create(long userId, BookingDto bookingDto);

    BookingDto updateStatus(long userId, Long bookingId, boolean approved);

    BookingDto getBooking(long userId, Long bookingId);

    List<BookingDto> getUserBookings(long userId, String state);

    List<BookingDto> getOwnerItemsBooked(long userId, String state);
}
