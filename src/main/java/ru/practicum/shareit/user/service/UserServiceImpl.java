package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserDto userDto) {
        try {
            checkEmail(userDto);
            return mapper.fromUser(userRepository.save(mapper.fromDto(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Что-то пошло не так");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        try {
            User update = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не существует"));

            if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
                update.setEmail(userDto.getEmail());
            }
            if (userDto.getName() != null && !userDto.getName().isBlank()) {
                update.setName(userDto.getName());
            }

            return mapper.fromUser(userRepository.save(update));
        } catch (DataIntegrityViolationException e) {
            throw new EntityNotFoundException("Что-то пошло не так");
        }
    }

    @Override
    public Optional<UserDto> getUser(Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не существует"));
        return Optional.of(mapper.fromUser(booker));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return mapper.toUserDto(users);
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    //дополнительыне методы
    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не существует"));
    }

    private void checkEmail(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new BadRequestException("Некорректный email");
        }
    }

}