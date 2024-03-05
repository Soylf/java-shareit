package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestServer {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAll(Long userId);

    List<ItemRequestDto> getAllByUser(int from, int size, long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
