package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long id, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long id, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(long userId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndBookingStatusOrderByStartDesc(long userId, BookingStatus waiting);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndBookingStatusOrderByStartDesc(long ownerId, BookingStatus rejected);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);
}
