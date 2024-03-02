package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServer;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = {ItemRequestController.class})
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestServer itemRequestService;
    @Autowired
    ObjectMapper objectMapper;
    ItemRequestDto itemRequestDto;
    List<ItemRequestDto> itemRequestListDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = createItemRequestDto();
        itemRequestListDto = createItemRequestDtoList();
    }













    private static ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("test")
                .items(List.of(ItemDto.builder().id(1L).build()))
                .build();
    }

    private static List<ItemRequestDto> createItemRequestDtoList() {
        return List.of(createItemRequestDto());
    }
}