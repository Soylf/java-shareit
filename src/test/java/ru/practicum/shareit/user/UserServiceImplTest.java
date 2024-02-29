package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

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

}