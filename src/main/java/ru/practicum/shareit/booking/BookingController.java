package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.server.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody BookingDto bookingDto) {
        return service.create(userId,bookingDto);
    }

    @PatchMapping
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable Long bookingId, @RequestParam boolean approved) {
        return service.updateStatus(userId,bookingId,approved);
    }

    @GetMapping
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id")
                                     long userId, @PathVariable Long bookingId) {
        return service.getBooking(userId,bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.getUserBookings(userId,state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerItemsBooked(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state) {
        return service.getOwnerItemsBooked(userId,state);
    }
}
