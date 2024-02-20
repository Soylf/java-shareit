package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public BookingDto fromBooking(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getBookingStatus())
                .build();
    }

    public List<BookingDto> toBookingTo(List<Booking> bookings) {
        return bookings.stream()
                .map(this::fromBooking)
                .collect(Collectors.toList());
    }

    public BookingResponseDto fromBookingDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public Booking toBookerTo(BookingRequestDto bookingRequestDto,
                              User booker, Item item,
                              BookingStatus bookingStatus) {
        return Booking.builder()
                .bookingStatus(bookingStatus)
                .booker(booker)
                .item(item)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }
}
