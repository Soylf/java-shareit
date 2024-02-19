package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public Booking fromDto(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .bookingStatus(bookingDto.getBookingStatus())
                .build();
    }

    public BookingDto fromBooking(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public List<BookingDto> toBookingTo(List<Booking> bookings) {
        return bookings.stream()
                .map(this::fromBooking)
                .collect(Collectors.toList());
    }

    public BookingDto fromBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public Booking toBookerTo(BookingDto bookingDto, User booker, Item item, BookingStatus bookingStatus) {
        return Booking.builder()
                .booker(booker)
                .item(item)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .bookingStatus(bookingStatus)
                .build();
    }
}
