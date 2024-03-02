package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Такого пользователя нет");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));

        itemDto.setOwner(user);
        return itemMapper.fromItem(itemRepository.save(itemMapper.fromDto(itemDto)));
    }

    @Override
    public CommentResponseDto addComment(CommentResponseDto commentResponseDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Обьект: " + userId + "  нет"));
        User user = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));

        List<Booking> bookings = bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(itemId, userId);


        if (!bookings.isEmpty()) {
            boolean hasActiveBooking = bookings.stream().anyMatch(booking -> booking.getBookingStatus() != BookingStatus.REJECTED && booking.getBookingStatus() != BookingStatus.WAITING && booking.getEnd().isBefore(LocalDateTime.now()));
            if (hasActiveBooking) {
                Comment comment = commentMapper.fromCommentTo(commentResponseDto, user, item);

                return commentMapper.toResponseDto(commentRepository.save(comment));
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
            Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException(("обьекта: " + itemId + " нет")));

            //Проверка на наличие
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getRequestId() != null) {
                item.setRequestId(itemDto.getRequestId());
            }

            return itemMapper.fromItem(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Что-то пошло не так");
        }
    }

    @Override
    public Optional<ItemDto> getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Предмета с ID " + itemId + " не существует"));
        List<CommentResponseDto> comments = commentMapper.toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        ItemDto itemDto = itemMapper.toItemResponseDto(item, null, null, comments);


        if (!item.getOwner().getId().equals(userId)) {
            return Optional.of(itemDto);
        }

        //Выведение последних и слудующих броней с дальнейшим употреблением
        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndBeforeAndBookingStatusOrderByEndDesc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);
        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndAfterAndBookingStatusOrderByEndAsc(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);

        if (!lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemDto.setLastBooking(bookingMapper.fromBookingDto(lastBooking.get(0)));
            itemDto.setNextBooking(bookingMapper.fromBookingDto(nextBooking.get(0)));
        } else if (lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemDto.setLastBooking(bookingMapper.fromBookingDto(nextBooking.get(0)));
            itemDto.setNextBooking(null);
        }

        return Optional.of(itemDto);
    }

    @Override
    public List<ItemDto> getAllItem(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Такого пользователя нет");
        }

        List<ItemDto> itemDos = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size);
        Page<Item> itemPage = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId, pageable);
        List<Item> items = itemPage.getContent();

        int endIndex = Math.min(from + size, items.size());
        for (int i = from; i < endIndex; i++) {
            Item item = items.get(i);
            List<CommentResponseDto> comments = commentMapper.toListComment(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId()));
            itemDos.add(itemMapper.toItemResponseDto(item, bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(item.getId(),
                    LocalDateTime.now()), bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()), comments));
        }

        return itemDos;
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return List.of();
        }


        Pageable pageable = PageRequest.of(from, size);
        Page<Item> itemPage = itemRepository.search(text, pageable);
        List<Item> items = itemPage.getContent();

        return itemMapper.toItemDos(items);
    }

    //Дополнительные методы
    private void checkUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }
}
