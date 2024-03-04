package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.server.BookingService;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    BookingDto bookingDto;
    BookingDto responseDto;
    BookingRequestDto createDto;


    @BeforeEach
    void setUp() {
        bookingDto = getTestBookingDto();
        responseDto = getResponseDto();
        createDto = getCreateDto();
    }

    @Test
    @SneakyThrows
    void getBookingByIdAndBookerShouldReturnStatusOkWhenDataIsValid() {
        when(bookingService.getBooking(eq(1L), eq(1L))).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getBookingByIdAndBookerIdShouldReturnStatusNotFoundWhenIdNotExists() {
        when(bookingService.getBooking(eq(0L), eq(0L)))
                .thenThrow(new EntityNotFoundException("Бронирование не найдено"));
        mockMvc.perform(get("/bookings/0")
                        .header("X-Sharer-User-Id", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createBookingShouldThrowBadRequestWhenRequestDataIsNotValid() {
        BookingRequestDto invalidBooking = BookingRequestDto.builder()
                .itemId(null)
                .start(null)
                .itemId(null)
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(invalidBooking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createBookingShouldReturnStatusCreatedWhenDataIsValid() {
        when(bookingService.create(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createBookingShouldReturnStatusBadRequestWhenDataHasNoItemId() {
        BookingRequestDto createDto = BookingRequestDto.builder()
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now())
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createBookingWhenShouldReturnStatusBadRequestWhenDataHasNoStartAndEndDate() {
        BookingRequestDto createDto = BookingRequestDto.builder()
                .itemId(1L)
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void createBookingShouldReturnStatusBadRequestWhenRequestDataEmpty() {
        BookingRequestDto createDto = BookingRequestDto.builder()
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void approveWhenParamFalseShouldReturnStatusOk() {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void approveWhenParamTrueThenStatusOk() {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void approveWhenBookingIdNotExistShouldThrowStatusBadRequest() {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenThrow(new EntityNotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/{bookingId}", 999L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void approveWhenUserNotExistShouldThrowStatusNotFound() {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenThrow(new EntityNotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 0L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getUserBookingsWhenDataHasEmptyListThenStatusOk() {
        when(bookingService.getAllUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();

    }

    @Test
    @SneakyThrows
    void getUserBookingsWhenListHaveOneBookingThenStatusOk() {
        List<BookingDto> bookingDtoList = getBookingDtos();

        when(bookingService.getAllOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)))
                .andDo(print())
                .andReturn();

    }

    private List<BookingDto> getBookingDtos() {
        return List.of(bookingDto);
    }

    private static BookingDto getTestBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now())
                .booker(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@email.com")
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .status(BookingStatus.WAITING)
                .build();
    }

    private static BookingDto getResponseDto() {
        return BookingDto.builder()
                .id(1L)
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@email.com")
                        .build())
                .end(LocalDateTime.now().plusDays(2))
                .start(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.WAITING)
                .build();
    }

    private static BookingRequestDto getCreateDto() {
        return BookingRequestDto.builder()
                .end(LocalDateTime.now().plusDays(2))
                .start(LocalDateTime.now().plusHours(1))
                .itemId(1L)
                .build();
    }
}