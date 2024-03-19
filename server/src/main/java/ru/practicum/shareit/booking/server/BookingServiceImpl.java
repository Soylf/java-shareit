package ru.practicum.shareit.booking.server;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        User booker = getUser(userId);
        Item item = getItem(bookingRequestDto.getItemId());

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) || bookingRequestDto.getStart().equals(bookingRequestDto.getEnd()) || bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Время бронирования неверное");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Вы не можете бронировать свою вещь");
        }

        Booking booking = mapper.fromBookingToItem(bookingRequestDto, booker, item, BookingStatus.WAITING);

        return mapper.fromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateStatus(long userId, Long bookingId, Boolean approved) {
        checkUser(userId);

        Booking booking = getBooking(bookingId);

        if (booking.getBooker().getId().equals(userId)) {
            throw new EntityNotFoundException("Только владелец вещи может подтвердить бронирование");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Только владелец вещи может подтвердить бронирование(Owner)");
        }
        if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Статус брони должен быть в ожидании - 'WAITING', другой статус подтвердить невозможно");
        }

        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.fromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, Long bookingId) {
        checkUser(userId);

        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());

        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new EntityNotFoundException("не найден пользователь: " + userId + " и бронь: " + bookingId);
        }
        return mapper.fromBooking(booking);
    }

    @Override
    public List<BookingDto> getAllOwner(long ownerId, String state, int from, int size) {
        checkUser(ownerId);

        if (from >= 0) {
            Pageable pageable = PageRequest.of(from / size, size);
            Page<Booking> bookings;
            switch (BookingStatus.convert(state)) {
                case ALL:
                    bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
                    break;
                default:
                    throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
            }
            List<Booking> bookingList = bookings.getContent();
            return mapper.toBookingTo(bookingList);
        }
        throw new BadRequestException("Что то пошло не так");
    }

    @Override
    public List<BookingDto> getAllUser(long userId, String state, int from, int size) {
        checkUser(userId);

        if (from >= 0) {
            Pageable pageable = PageRequest.of(from / size, size);
            Page<Booking> bookings;
            switch (BookingStatus.convert(state)) {
                case ALL:
                    bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageable);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                    break;
                default:
                    throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
            }

            List<Booking> bookingList = bookings.getContent();
            return mapper.toBookingTo(bookingList);
        }
        throw new BadRequestException("Что то пошло не так");
    }


    //Дополнительные методы
    private void checkUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("придмета: " + itemId + "  нет"));

    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("брони: " + bookingId + "  нет"));
    }
}
