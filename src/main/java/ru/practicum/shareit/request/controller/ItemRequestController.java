package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServer;

import javax.validation.Valid;
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
        return server.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestDto(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return server.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestDtoByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return server.getAllByUser(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestDtoByUser(@PathVariable Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return server.getById(userId, requestId);
    }
}
