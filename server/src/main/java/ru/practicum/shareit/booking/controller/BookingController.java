package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.server.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Получен запрос на создание брони от " + userId + " с такой броней {}", bookingRequestDto);
        return service.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                   @RequestParam(name = "approved", required = false) Boolean approved) {
        log.info("Полчен запрос на обновление бронирования от " + userId + " на бронь  {}", bookingId);
        return service.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long bookingId) {
        log.info("Поулчен запрос поулчение брони от " + userId + " по такому id {}", bookingId);
        return service.getBooking(userId, bookingId);
    }

    @GetMapping
    @Validated
    public List<BookingDto> getAllUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "ALL") String state,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос на получение брони от " + userId + " с таким вот статусом " + state);
        return service.getAllUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    @Validated
    public List<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("(owner)Получен запрос на получение брони от " + userId + " с таким вот статусом " + state);
        return service.getAllOwner(userId, state, from, size);
    }
}
