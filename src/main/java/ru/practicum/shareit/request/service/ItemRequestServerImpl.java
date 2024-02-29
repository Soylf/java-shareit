package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
        return mapper.fromItemRequestDto(repository.save(mapper.fromItemRequest(itemRequestDto,userId)));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        User user = getUser(userId);

        List<ItemRequestDto> requests = mapper.toItemRequestDto(repository.findAllByRequesterOrderByCreatedAsc(user));
        requests.forEach(requestDto -> requestDto.setItems(findAllByRequest(requestDto.getId())));
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(int from, int size, long userId) {
        User user = getUser(userId);

        List<ItemRequest> itemRequests = repository.findAllByRequesterOrderByCreatedAsc(user);

        //пагинация
        int startIndex = Math.min(from, itemRequests.size());
        int endIndex = Math.min(from + size, itemRequests.size());
        List<ItemRequest> requests = itemRequests.subList(startIndex, endIndex);

        return mapper.toItemRequestDto(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        checkUser(userId);
        ItemRequestDto itemRequestDto = mapper.fromItemRequestDto(getItemRequest(requestId));

        return null;
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

    private List<ItemDto> findAllByRequest(Long requestId) {
        return itemMapper.toItemDos(itemRepository.findAllByRequestId(requestId));
    }
}
