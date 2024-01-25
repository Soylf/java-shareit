package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto,Long userId);
    ItemDto updateItem(ItemDto itemDto, Long itemId);
    Optional<ItemDto> getItem(Long itemId);
    List<ItemDto> getAllItem(Long userId);
    List<ItemDto> searchItem(String text);
}
