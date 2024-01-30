package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Long, List<Item>> itemUsers;
    private final UserService userService;
    private final Map<Long, Item> items;
    private final ItemMapper mapper;
    private long itemId = 0;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        checkerItem(itemDto);
        checkUser(userId);

        itemDto.setId(++itemId);
        itemDto.setUserId(userId);
        Item newItem = mapper.fromDto(itemDto);
        itemUsers.compute(userId, (key, itemList) -> {
            if (itemList == null) {
                itemList = new ArrayList<>();
            }
            itemList.add(newItem);
            return itemList;
        });
        items.put(itemId, newItem);
        return itemDto;
    }

    @Override
    @SneakyThrows
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        checkUser(userId);

        if (!itemUsers.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не имеет никаких товаров");
        }

        for (Item itemUs : itemUsers.get(userId)) {
            if (!Objects.equals(itemUs.getId(), itemId)) {
                throw new EntityNotFoundException("Придмета с ID " + itemId + " не существует");
            }
        }

        if (items.containsKey(itemId)) {
            ItemDto updatedItemDto = mapper.fromItem(items.get(itemId));
            if (itemDto.getDescription() != null) {
                updatedItemDto.setDescription(itemDto.getDescription());
            }
            if (itemDto.getName() != null) {
                updatedItemDto.setName(itemDto.getName());
            }
            if (itemDto.getAvailable() != null) {
                updatedItemDto.setAvailable(itemDto.getAvailable());
            }
            Item updatedItem = mapper.fromDto(updatedItemDto);
            items.put(itemId, updatedItem);

            if (itemUsers.containsKey(userId)) {
                List<Item> userItems = itemUsers.get(userId);
                userItems.removeIf(i -> i.getId().equals(itemId));
            }

            itemUsers.computeIfAbsent(userId, k -> new ArrayList<>()).add(updatedItem);
            return updatedItemDto;
        } else {
            throw new EntityNotFoundException("Придмета с ID " + itemId + " не существует");
        }
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId) {
        Item item = items.get(itemId);
        return Optional.ofNullable(mapper.fromItem(item));
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        checkUser(userId);
        List<ItemDto> newItemDto = new ArrayList<>();

        for (Item item : itemUsers.get(userId)) {
            newItemDto.add(mapper.fromItem(item));
        }
        return newItemDto;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> foundItems = new ArrayList<>();

        if (!text.isBlank()) {
            for (Item item : items.values()) {
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

    @SneakyThrows
    private void checkUser(Long userId) {
        boolean userExists = userService.getUser(userId).isPresent();
        if (!userExists) {
            throw new EntityNotFoundException("Такого пользователя нет");
        }
    }

    private void checkerItem(ItemDto itemDto) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (name == null || name.isEmpty()) {
            throw new BadRequestException("Неверное название");
        }
        if (description == null || description.isEmpty()) {
            throw new BadRequestException("Неверное описание");
        }
        if (available == null) {
            throw new BadRequestException("Неверное запрос");
        }
    }
}
