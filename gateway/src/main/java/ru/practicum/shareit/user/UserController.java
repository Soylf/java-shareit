package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto,
                                         @PathVariable("userId") Long id) {
        checkValidUserForUpdate(userDto);
        return client.update(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) {
        return client.getUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUsers() {
        return client.getUsers();
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        return client.delete(id);
    }

    private void checkValidUserForUpdate(UserDto user) {
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !user.getEmail().matches(".+[@].+[\\.].+")) {
                throw new BadRequestException("Email");
            }
        }

        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                throw new BadRequestException("Name");
            }
        }
    }
}
