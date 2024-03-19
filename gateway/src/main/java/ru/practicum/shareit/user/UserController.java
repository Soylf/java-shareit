package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto,
                                         @PathVariable("userId") Long id) {
        checkUser(userDto);
        return client.update(id, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") Long id) {
        return client.getUser(id);
    }

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        return client.getUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Positive @PathVariable("userId") Long id) {
        return client.delete(id);
    }

    private void checkUser(UserDto user) {
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
