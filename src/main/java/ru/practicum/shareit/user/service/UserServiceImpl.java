package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserDto userDto) {
        if(userDto.getEmail() == null) {
            throw new BadRequestException("У пользователя нет почты");
        }

        checker(userDto);
        long id = generateNextId();
        userDto.setId(id);
        try {
            User user = mapper.fromDto(userDto);
            users.put(id, user);
            return userDto;
        } catch (BadRequestException e) {
            throw new BadRequestException("Проблема при создании пользователя");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        checker(userDto);
        userDto.setId(id);
        if (users.containsKey(id)) {
            User existingUser = users.get(id);
            UserDto updatedUserDto = mapper.fromUser(existingUser);

            if (userDto.getName() != null) {
                updatedUserDto.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                updatedUserDto.setEmail(userDto.getEmail());
            }

            User updatedUser = mapper.fromDto(updatedUserDto);
            users.put(id, updatedUser);
            return updatedUserDto;
        } else {
            throw new BadRequestException("Пользователь с ID " + id + " не существует");
        }
    }

    @Override
    public Optional<UserDto> getUser(Long id) {
        if (users.containsKey(id)) {
            UserDto userDto = mapper.fromUser(users.get(id));
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users.values()) {
            UserDto userDto = mapper.fromUser(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    private long generateNextId() {
        long maxId = users.keySet().stream().mapToLong(Long::valueOf).max().orElse(0L);
        return maxId + 1;
    }

    private void checker(UserDto userDto) throws BadRequestException {
        if(userDto.getEmail() != null) {
            boolean isEmailUnique = users.values().stream().noneMatch(user -> user.getEmail().equals(userDto.getEmail()));

            if (!userDto.getEmail().contains("@")) {
                throw new BadRequestException("Некорректный email");
            }
            if (!isEmailUnique) {
                throw new ValidationException("Email уже существует");
            }
        }
    }
}