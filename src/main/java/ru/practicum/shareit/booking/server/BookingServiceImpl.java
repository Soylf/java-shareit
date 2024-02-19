package ru.practicum.shareit.booking.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));

        if (bookingDto.getItem() == null) {
            throw new EntityNotFoundException("Предмет не указан");
        }

        Item item = itemRepository.findById(bookingDto.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("предмета: " + bookingDto.getItem().getId() + "  нет"));

        if (!item.getAvailable()) {
            throw new EntityNotFoundException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Вы не можете бронировать свою вещь");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new EntityNotFoundException("Время бронирования неверное");
        }

        Booking booking = mapper.toBookerTo(bookingDto,booker,item,BookingStatus.WAITING);

        return mapper.fromBooking(bookingRepository.save(booking));
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
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Только владелец вещи может подтвердить бронирование");
        }

        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.fromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, Long bookingId) {
        checkUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("брони: " + bookingId + "  нет"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("придмета: " + booking.getItem().getId() + "  нет"));

        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new EntityNotFoundException("не найден пользователь: " + userId + " и бронь: " + bookingId);
        }
        return mapper.fromBooking(booking);
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
    public List<BookingDto> getUserItemsBooked(long userId, BookingStatus state) {
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
    private void checkUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private void checkItem(long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("придмета: " + itemId + "  нет"));
    }
}
