package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @SneakyThrows
    void createUserShouldReturnCreatedUserWhenRequestDataIsValidAndStatusShouldBeOk() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@gmail.com")
                .build();

        when(userService.create(any())).thenReturn(testUser);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

        @Test
        @SneakyThrows
        void createUserShouldThrowBadRequestWhenRequestDataIsNotValid () {
            UserDto userDto = UserDto.builder()
                    .email("test@email.com")
                    .build();

            when(userService.create(any())).thenThrow(HttpClientErrorException.BadRequest.class);

            mvc.perform(post("/users")
                            .content(objectMapper.writeValueAsString(userDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @SneakyThrows
        void createUserShouldThrowBadRequestWhenEmailDataIsNotValid () {
            UserDto userDto = UserDto.builder()
                    .email("test")
                    .email("invalid.com")
                    .build();

            when(userService.create(any())).thenThrow(HttpClientErrorException.BadRequest.class);

            mvc.perform(post("/users")
                            .content(objectMapper.writeValueAsString(userDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @SneakyThrows
        void createUserShouldThrowBadRequestWhenRequestHaveEmptyBody () {
            UserDto userDto = UserDto.builder().build();

            when(userService.create(userDto)).thenThrow(HttpClientErrorException.BadRequest.class);

            mvc.perform(post("/users")
                            .content(objectMapper.writeValueAsString(userDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @SneakyThrows
        void readUserShouldReturnValidValuesAndStatusOk () {
            when(userService.getUser(1L)).thenReturn(Optional.ofNullable(testUser));
            mvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("test"))
                    .andExpect(jsonPath("$.email").value("test@gmail.com"))
                    .andDo(print());
            verify(userService, times(1)).getUser(1L);
        }

        @Test
        @SneakyThrows
        void readUsersShouldReturnListOfUsersAndStatusOk () {
            when(userService.getAllUsers()).thenReturn(getListOfTestUsers());
            mvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(content().json(objectMapper.writeValueAsString(getListOfTestUsers())))
                    .andDo(print());
        }

        @Test
        @SneakyThrows
        void readUsersShouldReturnListOfUsersWhenUserIsEmpty () {
            when(userService.getAllUsers()).thenReturn(getEmptyTestUser());
            mvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(content().json(objectMapper.writeValueAsString(getEmptyTestUser())))
                    .andDo(print());
        }


        @Test
        @SneakyThrows
        void deleteUserShouldReturnStatusOk () {
            mvc.perform(delete("/users/1"))
                    .andExpect(status().is2xxSuccessful())
                    .andDo(print());
        }


        @Test
        @SneakyThrows
        @DisplayName("Проверка контроллера")
        void getUser_whenInvokeIncorrectId_thenStatusNotFound () {
            when(userService.getUser(-1L))
                    .thenThrow(EntityNotFoundException.class);

            mvc.perform(get("/users/-1"))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        private static List<UserDto> getEmptyTestUser () {
            return List.of(UserDto.builder().build());
        }

        private static List<UserDto> getListOfTestUsers () {
            return List.of(UserDto.builder()
                            .id(2L)
                            .name("second")
                            .email("mail2@gmail.com")
                            .build(),
                    UserDto.builder()
                            .id(3L)
                            .name("third")
                            .email("mail3@gmail.com")
                            .build());
        }
    }