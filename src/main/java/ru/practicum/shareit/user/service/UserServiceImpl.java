package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users;
    private final UserMapper mapper;
    private long maxId = 0;

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new BadRequestException("У пользователя нет почты");
        }
        checkerCreate(userDto);
        userDto.setId(++maxId);
        try {
            User user = mapper.fromDto(userDto);
            users.put(maxId, user);
            return userDto;
        } catch (BadRequestException e) {
            throw new BadRequestException("Проблема при создании пользователя");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        userDto.setId(id);
        User existingUser = users.get(id);
        if (existingUser != null) {
            UserDto updatedUserDto = mapper.fromUser(existingUser);
            checkerUpdate(userDto);
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
        User user = users.get(id);
        if (user != null) {
            UserDto userDto = mapper.fromUser(user);
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

    //Дополнительные методы
    private void checkerCreate(UserDto userDto) throws BadRequestException {
        boolean isEmailUnique = users.values().stream().noneMatch(user -> user.getEmail().equals(userDto.getEmail()));

        if (!userDto.getEmail().contains("@")) {
            throw new BadRequestException("Некорректный email");
        }
        if (!isEmailUnique) {
            throw new ValidationException("Email уже существует");
        }
    }

    private void checkerUpdate(UserDto userDto) throws BadRequestException {
        if (userDto.getEmail() != null) {
            boolean isEmailUnique = users.values().stream().filter(u -> !u.getId().equals(userDto.getId())) // исключаем из поиска текущего пользователя
                    .noneMatch(u -> u.getEmail().equals(userDto.getEmail()));

            if (!userDto.getEmail().contains("@")) {
                throw new BadRequestException("Некорректный email");
            }
            if (!isEmailUnique) {
                throw new ValidationException("Email уже существует");
            }
        }
    }
}