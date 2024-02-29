package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.server.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Получен запрос на создание брони от " + userId + " с такой броней {}", bookingRequestDto);
        return service.create(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                   @RequestParam(name = "approved", required = false) Boolean approved) {
        log.info("Полчен запрос на обновление бронирования от " + userId + " на бронь  {}", bookingId);
        return service.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id")
                                 long userId, @PathVariable Long bookingId) {
        log.info("Поулчен запрос поулчение брони от " + userId + " по такому id {}", bookingId);
        return service.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "ALL") String state,
                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                       @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        log.info("Получен запрос на получение брони от " + userId + " с таким вот статусом " + state);
        return service.getAllUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        log.info("(owner)Получен запрос на получение брони от " + userId + " с таким вот статусом " + state);
        return service.getAllOwner(userId, state, from, size);
    }
}
