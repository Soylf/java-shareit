package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    UserMapper mapper;
    UserService userService;
    UserDto userDto;
    User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, mapper);
        userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();

        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();
    }

    @Test
    @DisplayName("create - должен сохранять пользвателя в базу")
    void createShouldSaveWhenRequestHasValidData() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userSave = userService.create(userDto);

        assertEquals(user.getEmail(), userSave.getEmail());
        assertEquals(user.getName(), userSave.getName());

    }

    @Test
    @DisplayName("update - должен обновить email и name ")
    void updateShouldUpdateNameAndEmailWhenRequestHasDataToUpdateNameAndEmail() {
        User testUpdate = User.builder()
                .id(1L)
                .name("update")
                .email("update@gmail.com")
                .build();

        when(userRepository.saveAndFlush(any())).thenReturn(testUpdate);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userSave = userService.update(mapper.fromUser(testUpdate), user.getId());

        assertEquals(testUpdate.getEmail(), userSave.getEmail());
        assertEquals(testUpdate.getName(), userSave.getName());
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    @DisplayName("update - должен выбрасывать EntityNotFoundException если пользователь не найден")
    void updateShouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        User userUpdate = User.builder()
                .id(1L)
                .name("update")
                .email("update@gmail.com")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException EntityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.update(mapper.fromUser(userUpdate), userUpdate.getId()));

        assertEquals("Невозможно обновить данные пользователя с id =  1 - пользователь с таким id не найден в базе.", EntityNotFoundException.getMessage());
        verify(userRepository, times(0)).save(user);
        verify(userRepository, times(1)).findById(userUpdate.getId());
    }

    @Test
    @DisplayName("update - должен выбрасывать AlreadyExistException")
    public void update_ThrowsAlreadyExistException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("findById - должен возвращать правильного пользователя")
    void findByIdShouldReturnValidUserWhenRequestDataIsValid() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        Optional<UserDto> userDto = userService.getUser(user.getId());

        assertEquals(user.getEmail(), userDto.get().getEmail());
        assertEquals(user.getName(), userDto.get().getEmail());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("findById - должен выбрасывать EntityNotFoundException")
    void findByIdShouldThrowEntityNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException EntityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getUser(user.getId()));

        assertEquals("Пользователь с id 1 не найден", EntityNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("deleteUser - должен удалять из базы")
    void deleteUserShouldDelete() {
        when(userRepository.existsById(any())).thenReturn(true);

        userService.deleteUserById(user.getId());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).existsById(user.getId());
    }

    @Test
    @DisplayName("deleteUser - должен выбрасывать EntityNotFoundException")
    void deleteUserShouldThrowEntityNotFoundExceptionWhenUserNotExists() {
        when(userRepository.existsById(eq(user.getId()))).thenReturn(false);

        EntityNotFoundException EntityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUserById(user.getId()));

        assertEquals("Пользователь с id 1 не найден. Удаление невозможно.", EntityNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).deleteById(user.getId());
    }

    @Test
    @DisplayName("readUser - должен возвращать список пользователей")
    void readUsersShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(1, userDtoList.size());
    }

    @Test
    @DisplayName("readAll - размер возвращаемого листа должен быть 0")
    void readAllListSizeShouldBe0WhenUsersListIsEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(0, userDtoList.size());
    }
}