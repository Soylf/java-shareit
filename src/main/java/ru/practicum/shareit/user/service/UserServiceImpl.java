package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(mapper.fromDto(userDto));
        return mapper.fromUser(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        checkUser(userId);

        return mapper.fromUser(userRepository.save(mapper.fromDto(userDto)));
    }

    @Override
    public Optional<UserDto> getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не существует"));
        return Optional.of(mapper.fromUser(user));
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
}