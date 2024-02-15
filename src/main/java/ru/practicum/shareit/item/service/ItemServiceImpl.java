package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        checkUser(userId);

        Item item = mapper.fromDto(itemDto);
        item.setUserId(userId);
        return mapper.fromItem(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        checkItem(itemId);
        checkUser(userId);

        itemRepository.updateItemFields(mapper.fromDto(itemDto), itemId, userId);
        return mapper.fromItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Придмета с ID " + itemDto + " не существует")));
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Придмета с ID " + itemId + " не существует"));
        return Optional.of(mapper.fromItem(item));
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        List<Item> items = itemRepository.findAll();
        return mapper.toItemDto(items);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> foundItems = new ArrayList<>();

        if (!text.isBlank()) {
            for (Item item : itemRepository.findAll()) {
                if (itemContains(item, text) && item.getAvailable()) {
                    foundItems.add(mapper.fromItem(item));
                }
            }
        }
        return foundItems;
    }

    //Дополнительные методы

    private boolean itemContains(Item item, String text) {
        return item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase());
    }
    private void checkUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("пользователя: " + userId + "  нет"));
    }

    private void checkItem(long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new BadRequestException(("обьекта: " + itemId + " нет")));
    }
}
