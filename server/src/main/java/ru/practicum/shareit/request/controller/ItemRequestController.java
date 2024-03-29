package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServer;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestServer server;


    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание публикации " + itemRequestDto + " от " + userId);
        return server.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestDto(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получения списка ответа на публикацию от + " + userId);
        return server.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestDtoByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
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
