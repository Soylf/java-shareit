package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServer;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestServer server;

    @Autowired
    public ItemRequestController(ItemRequestServer server) {
        this.server = server;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание публикации " + itemRequestDto + " от " + userId);
        return server.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestDto(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получения списка ответа на публикацию от + " + userId);
        return server.getAll(userId);
    }

    @GetMapping("/all")
    @Validated
    public List<ItemRequestDto> getAllItemRequestDtoByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        log.info("Поулчен запрос на получение списка публикация от других пользовтелей: вот некотоыре данные \n"
                + " Пользователь " + userId + "\n Начало списка " + from + "\n Конец списка " + size);
        return server.getAllByUser(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestDtoByUser(@PathVariable Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на обработку конкретной публикации " + requestId + " от " + userId);
        return server.getById(userId, requestId);
    }
}
