package ru.practicum.shareit.booking.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        bookingDto.setBooker(checkUser(userId));
        bookingDto.setBookingStatus(BookingStatus.WAITING);
        return mapper.fromBooking(bookingRepository.save(mapper.fromDto(bookingDto)));
    }

    @Override
    public BookingDto updateStatus(long userId, Long bookingId, boolean approved) {
        checkUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("брони: " + userId + "  нет"));

        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.fromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, Long bookingId) {
        for (Booking booking : bookingRepository.findAllBookingsSortedByUserId(userId)) {
            if (booking.getId().equals(bookingId)) {
                return mapper.fromBooking(booking);
            }
        }
        throw new BadRequestException("Booking not found for userId: " + userId + " and bookingId: " + bookingId);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingStatus state) {
        List<Booking> bookings;

        if (state == BookingStatus.ALL) {
            bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        } else {
            bookings = bookingRepository.findAllByBookerIdAndBookingStatusOrderByStartDesc(userId, state);
        }

        return mapper.toBookingTo(bookings);
    }

    @Override
    public List<BookingDto> getUserItemsBooked(long userId, BookingStatus state) throws EntityNotFoundException {
        List<Booking> bookings;

        if (state == BookingStatus.ALL) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        } else {
            bookings = bookingRepository.findAllByItemOwnerIdAndBookingStatusOrderByStartDesc(userId, state);
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("У него нефига нет: " + userId);
        }

        return mapper.toBookingTo(bookings);
    }

    //Дополнительные методы
    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("пользователя: " + userId + "  нет"));
    }
}
