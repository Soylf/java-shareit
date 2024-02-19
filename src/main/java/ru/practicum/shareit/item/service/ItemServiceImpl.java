package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));

            itemDto.setOwner(user);
            return itemMapper.fromItem(itemRepository.save(itemMapper.fromDto(itemDto)));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Что-то пошло не так");
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Обьект: " + userId + "  нет"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));

        List<Booking> bookings = bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(itemId, userId);

        if (!bookings.isEmpty()) {
            boolean hasActiveBooking = bookings.stream()
                    .anyMatch(booking -> booking.getBookingStatus() != BookingStatus.REJECTED
                            && booking.getBookingStatus() != BookingStatus.WAITING
                            && booking.getEnd().isBefore(LocalDateTime.now()));
            if (hasActiveBooking) {
                Comment comment = commentMapper.fromCommentTo(commentDto, user, item);
                return commentMapper.fromComment(commentRepository.save(comment));
            } else {
                throw new BadRequestException("Нет активного бронирования для данного предмета");
            }
        } else {
            throw new BadRequestException("Что-то пошло не так");
        }
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        checkUser(userId);

        try {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException(("обьекта: " + itemId + " нет")));


            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }

            return itemMapper.fromItem(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Что-то пошло не так");
        }
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмета с ID " + itemId + " не существует"));
        List<CommentDto> comments = commentMapper.toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        ItemDto itemDto = itemMapper.toItemDtoAndCommits(item, null, null, comments);


        if (!item.getOwner().getId().equals(userId)) {
            return Optional.of(itemDto);
        }


        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndBookingStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(DESC, "end"));
        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndBookingStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        if (lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemDto.setLastBookingDate(bookingMapper.fromBookingDto(nextBooking.get(0)));
            itemDto.setNextBookingDate(null);
        } else if (!lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemDto.setNextBookingDate(bookingMapper.fromBookingDto(nextBooking.get(0)));
            itemDto.setLastBookingDate(bookingMapper.fromBookingDto(lastBooking.get(0)));
        }

        return Optional.of(itemDto);
    }

    @Override
    public List<ItemDto> getAllItem(Long userId) {
        List<ItemDto> itemDos = new ArrayList<>();
        List<Item> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Такого пользователя нет");
        }

        for (Item item : items) {
            List<CommentDto> comments = commentMapper
                    .toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()));

            Booking lastBooking = bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
            Booking nextBooking = bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());

            itemDos.add(itemMapper.toItemDtoAndCommits(item,
                    lastBooking != null ? bookingMapper.fromBooking(lastBooking) : null,
                    nextBooking != null ? bookingMapper.fromBooking(nextBooking) : null,
                    comments));
        }

        return itemDos;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemMapper.toItemDos(itemRepository.search(text));
    }

    //Дополнительные методы
    private void checkUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }
}
