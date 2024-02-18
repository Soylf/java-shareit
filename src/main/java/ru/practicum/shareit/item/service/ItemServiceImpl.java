package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;


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

        item.setLastBookingDate(bookingRepository.getLastBookingDateForItem(itemId));
        item.setNextBookingDate(bookingRepository.getNextBookingDateForItem(itemId));

        // Получение комментариев для указанного предмета
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentDos = comments.stream()
                .map(commentMapper::fromComment)
                .collect(Collectors.toList());

        ItemDto itemDto = itemMapper.fromItem(item);
        itemDto.setCommentDos(commentDos);

        return Optional.of(itemDto);
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        for (Item item : items) {
            item.setLastBookingDate(bookingRepository.getLastBookingDateForItem(item.getId()));
            item.setNextBookingDate(bookingRepository.getNextBookingDateForItem(item.getId()));

            // Получение комментариев для каждого предмета
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            List<CommentDto> commentDos = comments.stream()
                    .map(commentMapper::fromComment)
                    .collect(Collectors.toList());

            ItemDto itemDto = itemMapper.fromItem(item);
            itemDto.setCommentDos(commentDos);
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
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private void checkItem(long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(("обьекта: " + itemId + " нет")));
    }
}
