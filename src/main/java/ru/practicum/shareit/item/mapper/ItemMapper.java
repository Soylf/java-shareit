package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository requestRepository;

    public Item fromDto(ItemDto itemDto) {
        Item.ItemBuilder itemBuilder = Item.builder()
                .id(itemDto.getId())
                .owner(itemDto.getOwner())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable());

        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            itemBuilder.request(itemRequest(requestId));
        }

        return itemBuilder.build();
    }

    public ItemDto fromItem(Item item) {
        ItemDto.ItemDtoBuilder itemDtoBuilder = ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable());

        ItemRequest request = item.getRequest();
        if (request != null) {
            itemDtoBuilder.requestId(request.getId());
        }

        return itemDtoBuilder.build();
    }

    public List<ItemDto> toItemDos(List<Item> item) {
        return item.stream()
                .map(this::fromItem)
                .collect(Collectors.toList());
    }

    public ItemDto toItemResponseDto(Item item,
                                     Booking lastBooking,
                                     Booking nextBooking,
                                     List<CommentResponseDto> comments) {
        ItemDto.ItemDtoBuilder itemDtoBuilder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .lastBooking(lastBooking == null ? null : bookingMapper.fromBookingDto(lastBooking))
                .nextBooking(nextBooking == null ? null : bookingMapper.fromBookingDto(nextBooking))
                .comments(comments);

        ItemRequest request = item.getRequest();
        if (request != null) {
            itemDtoBuilder.requestId(request.getId());
        }

        return itemDtoBuilder.build();
    }

    private ItemRequest itemRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Обьекта: " + id + "  нет"));
    }
}