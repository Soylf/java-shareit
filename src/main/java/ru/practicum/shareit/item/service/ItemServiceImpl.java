package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        checkUser(userId);

        Item item = itemMapper.fromDto(itemDto);
        item.setOwnerId(userId);
        return itemMapper.fromItem(item);
    }

    @Override
    public void addComment(CommentDto commentDto, Long itemId, Long userId) {
        checkItem(itemId);
        checkUser(userId);

        Comment comment = commentMapper.fromDto(commentDto);
        comment.setAuthor(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет")));
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(("обьекта: " + itemId + " нет"))));
        commentRepository.save(comment);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        checkItem(itemId);
        checkUser(userId);
        Item item = itemRepository.save(itemMapper.fromDto(itemDto));
        return itemMapper.fromItem(item);
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId, Long userId) {
        checkUser(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмета с ID " + itemId + " не существует"));
        List<CommentDto> comments = commentMapper.toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        ItemDto itemDto = itemMapper.toItemDtoAndCommits(item, null, null, comments);


        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(DESC, "end"));
        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        if (lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemDto.setLastBookingDate(bookingMapper.toBookingDto(nextBooking.get(0)));
            itemDto.setNextBookingDate(null);
        } else if (!lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemDto.setNextBookingDate(bookingMapper.toBookingDto(nextBooking.get(0)));
            itemDto.setLastBookingDate(bookingMapper.toBookingDto(lastBooking.get(0)));
        }

        return Optional.of(itemDto);
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        List<ItemDto> itemDos = new ArrayList<>();

        List<Item> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);

        for (Item item : items) {
            List<CommentDto> comments = commentMapper
                    .toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()));
            itemDos.add(itemMapper.toItemDtoAndCommits(item,
                    bookingMapper.fromBooking(bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now())),
                    bookingMapper.fromBooking(bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now())),
                    comments));
        }

        return itemDos;
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
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private void checkItem(long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(("обьекта: " + itemId + " нет")));
    }
}
