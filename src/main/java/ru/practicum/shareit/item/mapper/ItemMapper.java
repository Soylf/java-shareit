package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingMapper bookingMapper;

    public Item fromDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .owner(itemDto.getOwner())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto fromItem(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemDto toItemDtoAndCommits(Item item,
                                       BookingDto lastBooking,
                                       BookingDto nextBooking,
                                       List<CommentDto> commentDos) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .lastBookingDate(lastBooking)
                .nextBookingDate(nextBooking)
                .commentDos(commentDos)
                .build();
    }

    public List<ItemDto> toItemDos (List<Item> item) {
        return item.stream()
                .map(this::fromItem)
                .collect(Collectors.toList());
    }
}