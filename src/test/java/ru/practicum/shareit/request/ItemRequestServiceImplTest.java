package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServerImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserService userService;
    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestServerImpl requestService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request description")
            .build();

    @Test
    void addNewRequest() {
        ItemRequestDto requestDto = mapper.fromItemRequestDto(request);
        ItemRequestDto expectedRequestDto = mapper.fromItemRequestDto(request);
        when(userService.getUser(user.getId())).thenReturn(Optional.ofNullable(userDto));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto actualRequestDto = requestService.create(user.getId(), requestDto);

        assertEquals(expectedRequestDto, actualRequestDto);
    }


    @Test
    void getAllRequests() {
        List<ItemRequestDto> expectedRequestsDto = List.of(mapper.fromItemRequestDto(request));
        when(userService.getUser(user.getId())).thenReturn(Optional.ofNullable(userDto));
        when(requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn((Page<ItemRequest>) List.of(request));

        List<ItemRequestDto> actualRequestsDto = requestService.getAllByUser(0, 10, userDto.getId());

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getRequestById() {
        ItemRequestDto expectedRequestDto = mapper.fromItemRequestDto(request);
        when(userService.getUser(user.getId())).thenReturn(Optional.ofNullable(userDto));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequestDto actualRequestDto = requestService.getById(userDto.getId(), request.getId());

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        when(userService.getUser(user.getId())).thenReturn(Optional.ofNullable(userDto));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        ChangeSetPersister.NotFoundException requestNotFoundException = assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> requestService.getById(userDto.getId(), request.getId()));

        assertEquals(requestNotFoundException.getMessage(), String.format("Запрос с id: %s" +
                " не был найден.", request.getId()));
    }
}