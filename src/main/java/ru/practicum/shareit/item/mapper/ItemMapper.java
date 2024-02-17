package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .ownerId(itemDto.getOwnerId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .lastBookingDate(itemDto.getLastBookingDate())
                .nextBookingDate(itemDto.getNextBookingDate())
                .build();
    }

    public ItemDto fromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBookingDate(item.getLastBookingDate())
                .nextBookingDate(item.getNextBookingDate())
                .build();
    }

    public List<ItemDto> toItemDto(List<Item> items) {
        return items.stream()
                .map(this::fromItem)
                .collect(Collectors.toList());
    }

}