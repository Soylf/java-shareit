package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllBookingsSortedByUserId(long userId);

    List<Booking> findAllByBookerIdAndBookingStatusOrderByStartDesc(long userId, BookingStatus state);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndBookingStatusOrderByStartDesc(long userId, BookingStatus state);

    LocalDateTime getLastBookingDateForItem(Long itemId);

    LocalDateTime getNextBookingDateForItem(Long itemId);
}
