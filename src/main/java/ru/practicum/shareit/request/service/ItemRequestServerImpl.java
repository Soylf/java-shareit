package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServerImpl implements ItemRequestServer {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        checkUser(userId);

        itemRequestDto.setRequester(getUser(userId));
        return mapper.fromItemRequestDto(repository.save(mapper.fromItemRequest(itemRequestDto, userId)));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        User user = getUser(userId);

        List<ItemRequestDto> requests = mapper.toItemRequestDto(repository.findAllByRequesterOrderByCreatedAsc(user));
        requests.forEach(requestDto -> requestDto.setItems(findAllByRequest(getItemRequest(requestDto.getId()))));
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(int from, int size, long userId) {
        checkUser(userId);
        if (from >= 0) {
            Page<ItemRequest> itemRequests = repository.findAllByRequester_IdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));
            List<ItemRequestDto> requests = mapper.toItemRequestDto(itemRequests.getContent());
            requests.forEach(requestDto -> requestDto.setItems(findAllByRequest(getItemRequest(requestDto.getId()))));
            return requests;
        }
        throw new BadRequestException("Что то пошло не так");
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        checkUser(userId);
        ItemRequestDto itemRequestDto = mapper.fromItemRequestDto(getItemRequest(requestId));
        itemRequestDto.setItems(findAllByRequest(getItemRequest(requestId)));
        return itemRequestDto;
    }

    //Дополнительные методы
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private ItemRequest getItemRequest(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }

    private List<ItemDto> findAllByRequest(ItemRequest request) {
        return itemMapper.toItemDos(itemRepository.findAllByRequest(request));
    }
}
