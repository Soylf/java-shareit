package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        checkUser(userId);

        Item item = itemMapper.fromDto(itemDto);
        item.setOwnerId(userId);
        return itemMapper.fromItem(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        checkItem(itemId);
        checkUser(userId);
        Item item = itemRepository.save(itemMapper.fromDto(itemDto));
        return itemMapper.fromItem(item);
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Предмета с ID " + itemId + " не существует"));


        item.setLastBookingDate(bookingRepository.getLastBookingDateForItem(itemId));
        item.setNextBookingDate(bookingRepository.getNextBookingDateForItem(itemId));

        return Optional.of(itemMapper.fromItem(item));
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        List<Item> items = itemRepository.findAll();

        for (Item item : items) {
            item.setLastBookingDate(bookingRepository.getLastBookingDateForItem(item.getId()));
            item.setNextBookingDate(bookingRepository.getNextBookingDateForItem(item.getId()));
        }

        return itemMapper.toItemDto(items);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> foundItems = new ArrayList<>();

        if (!text.isBlank()) {
            for (Item item : itemRepository.findAll()) {
                if (itemContains(item, text) && item.getAvailable()) {
                    foundItems.add(itemMapper.fromItem(item));
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
