package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndBookingStatusOrderByStartDesc(long userId, BookingStatus state);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndBookingStatusOrderByStartDesc(long userId, BookingStatus state);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(Long itemId, LocalDateTime now, BookingStatus approved, Sort end);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long id, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long id, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);
}
