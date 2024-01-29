package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .userId(itemDto.getUserId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto fromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

}