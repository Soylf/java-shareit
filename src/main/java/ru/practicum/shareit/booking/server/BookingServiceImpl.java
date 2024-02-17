package ru.practicum.shareit.booking.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        User booker = checkUser(userId);
        Item item = checkItem(bookingDto.getId());

        bookingDto.setBooker(booker);
        bookingDto.setBookingStatus(BookingStatus.WAITING);

        if (!item.getAvailable()) {
            throw new EntityNotFoundException("Вещь недоступна для бронирования");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Вы не можете бронировать свою вещь");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new EntityNotFoundException("Время бронировани неверное");
        }

        return mapper.fromBooking(bookingRepository.save(mapper.fromDto(bookingDto)));
    }

    @Override
    public BookingDto updateStatus(long userId, Long bookingId, boolean approved) {
        checkUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("брони: " + userId + "  нет"));

        if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new EntityNotFoundException("Статус брони должен быть в ожидании - 'WAITING', другой статус подтвердить невозможно");
        }
        if (booking.getBooker().getId().equals(userId)) {
            throw new EntityNotFoundException("Только владелец вещи может подтвердить бронирование");
        }
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Только владелец вещи может подтвердить бронирование");
        }

        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.fromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, Long bookingId) {
        checkUser(userId);

        for (Booking booking : bookingRepository.findAllBookingsSortedByUserId(userId)) {
            if (booking.getId().equals(bookingId)) {
                return mapper.fromBooking(booking);
            }
        }
        throw new EntityNotFoundException("не найден пользователь: " + userId + " и бронь: " + bookingId);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingStatus state) {
        checkUser(userId);

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
        checkUser(userId);

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
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("придмета: " + itemId + "  нет"));
    }
}
