package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
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

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = getUser(userId);

        itemRequestDto.setRequester(user);
        return mapper.fromItemRequestDto(repository.save(mapper.fromItemRequest(itemRequestDto)));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(int from, int size, long userId) {
        return null;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        return null;
    }

    //Дополнительные методы
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("пользователя: " + userId + "  нет"));
    }
}
