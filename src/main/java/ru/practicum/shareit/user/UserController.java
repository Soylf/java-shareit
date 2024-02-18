package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("запуска метода create_User с такими вот данными: \n" + userDto);
        return service.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable("userId") Long id) {
        log.info("Запуск метода updateUser вот с такими данными: \n" + userDto);
        return service.update(userDto, id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {
        log.info("Запуск getUser");
        return service.getUser(id).orElse(null);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запуск getAllUser");
        return service.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public boolean deleteUserById(@PathVariable("id") Long id) {
        log.info("Запуск deleteUser");
        service.deleteUserById(id);
        return true;
    }
}