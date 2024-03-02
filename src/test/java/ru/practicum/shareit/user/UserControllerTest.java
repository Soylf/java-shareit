package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    UserDto testUser;
    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        testUser = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .build();
    }

}
