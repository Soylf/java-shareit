package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

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
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto,
                                         @PathVariable("userId") Long id) {
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

    @DeleteMapping
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        return client.delete(id);
    }
}
