package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final Map<Long, Item> item;
    private final ItemMapper mapper;
    private long maxId = 0;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        return null;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId) {
        return null;
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId) {
        return Optional.empty();
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        return null;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return null;
    }
}
