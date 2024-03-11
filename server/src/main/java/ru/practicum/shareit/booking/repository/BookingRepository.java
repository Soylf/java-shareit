package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long id, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long id, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);

    Page<Booking> findAllByBooker_IdOrderByStartDesc(long userId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndBookingStatusOrderByStartDesc(long userId, BookingStatus waiting, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndBookingStatusOrderByStartDesc(long ownerId, BookingStatus rejected, Pageable pageable);

    List<Booking> findTop1BookingByItemIdAndEndBeforeAndBookingStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus approved);

    List<Booking> findTop1BookingByItemIdAndEndAfterAndBookingStatusOrderByEndAsc(Long itemId, LocalDateTime now, BookingStatus approved);
}
