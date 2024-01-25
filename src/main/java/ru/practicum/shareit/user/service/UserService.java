package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto,Long id);
    Optional<UserDto> getUser(Long id);
    List<UserDto> getAllUsers();
    void deleteUserById(Long userId);
}
