package ru.practicum.shareit.booking.server;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    public BookingDto create(long userId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("предмета: " + bookingRequestDto.getItemId() + "  нет"));

        if (!item.getAvailable()) {
            throw new EntityNotFoundException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Вы не можете бронировать свою вещь");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())
                || bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())
                || bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new EntityNotFoundException("Время бронирования неверное");
        }

        Booking booking = mapper.toBookerTo(bookingRequestDto, booker, item, BookingStatus.WAITING);

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

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndBookingStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Invalid booking status: " + state);
        }

        return mapper.toBookingTo(bookings);
    }

    @Override
    public List<BookingDto> getUserItemsBooked(long userId, BookingStatus state) {
        checkUser(userId);

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadRequestException("Что-то пошло не так");
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
