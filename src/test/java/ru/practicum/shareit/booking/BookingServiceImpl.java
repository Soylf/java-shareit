package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.server.BookingServiceImpl;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Autowired
    private BookingMapper mapper;

    private final User user = User.builder().id(1L).name("username").email("email@email.com").build();

    private final User owner = User.builder().id(2L).name("username2").email("email2@email.com").build();

    private final UserDto userDto = UserDto.builder().id(1L).name("username").email("email@email.com").build();

    private final Item item = Item.builder().id(1L).name("item name").description("description").available(true).owner(owner).build();

    private final Booking booking = Booking.builder().id(1L).start(LocalDateTime.now().plusDays(1L)).end(LocalDateTime.now().plusDays(2L)).bookingStatus(BookingStatus.APPROVED).item(item).booker(user).build();

    private final Booking bookingWaiting = Booking.builder().id(1L).start(LocalDateTime.now().plusDays(1L)).end(LocalDateTime.now().plusDays(2L)).bookingStatus(BookingStatus.WAITING).item(item).booker(user).build();

    private final BookingRequestDto bookingRequestDto = BookingRequestDto.builder().itemId(any()).start(LocalDateTime.now().plusDays(1L)).end(LocalDateTime.now().plusDays(2L)).build();


    @Test
    void createWhenEndIsBeforeStartShouldThrowValidationException() {
        when(userService.getUser(userDto.getId())).thenReturn(Optional.of(userDto));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class, () -> bookingService.create(userDto.getId(), bookingRequestDto));

        assertEquals(bookingValidationException.getMessage(), "Дата окончания не может быть раньше или равна дате начала");
    }

    @Test
    void createWhenItemIsNotAvailableShouldThrowValidationException() {
        item.setAvailable(false);
        when(userService.getUser(userDto.getId())).thenReturn(Optional.of(userDto));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class, () -> bookingService.create(userDto.getId(), bookingRequestDto));

        assertEquals(bookingValidationException.getMessage(), "Вещь не доступна для бронирования.");
    }

    @Test
    void createWhenItemOwnerEqualsBookerShouldThrowValidationException() {
        item.setOwner(user);
        when(userService.getUser(userDto.getId())).thenReturn(Optional.of(userDto));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ChangeSetPersister.NotFoundException bookingNotFoundException = assertThrows(ChangeSetPersister.NotFoundException.class, () -> bookingService.create(userDto.getId(), bookingRequestDto));

        assertEquals(bookingNotFoundException.getMessage(), "Вещь не найдена.");
    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingDto actualBookingDto = bookingService.updateStatus(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(BookingStatus.APPROVED, actualBookingDto.getStatus());
    }

    @Test
    void getByIdWhenUserIsNotItemOwnerShouldThrowObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ChangeSetPersister.NotFoundException bookingNotFoundException = assertThrows(ChangeSetPersister.NotFoundException.class, () -> bookingService.getBooking(3L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Пользователь не владелeц и не автор бронирования ");
    }

    @Test
    void getAllByOwnerWhenBookingStateCURRENT() {
        List<BookingDto> expectedBookingsDtoOut = List.of(mapper.fromBooking(booking));
        when(userService.getUser(user.getId())).thenReturn(Optional.ofNullable(userDto));

        List<BookingDto> actualBookingsDtoOut = bookingService.getAllOwner(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStatePAST() {
        List<BookingDto> expectedBookingsDtoOut = List.of(mapper.fromBooking(booking));
        when(userService.getUser(user.getId())).thenReturn(Optional.ofNullable(userDto));

        List<BookingDto> actualBookingsDtoOut = bookingService.getAllOwner(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }
}
