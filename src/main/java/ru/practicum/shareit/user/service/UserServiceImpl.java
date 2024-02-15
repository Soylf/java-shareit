package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(mapper.fromDto(userDto));
        return mapper.fromUser(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        userRepository.updateUserFields(mapper.fromDto(userDto), id);
        return mapper.fromUser(userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Пользователь с ID " + id + " не существует")));
    }

    @Transactional
    @Override
    public Optional<UserDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Пользователь с ID " + id + " не существует"));
        return Optional.of(mapper.fromUser(user));
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return  mapper.toUserDto(users);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(()-> new BadRequestException("Пользователь с ID " + userId + " не существует"));
        userRepository.deleteById(userId);
    }
}