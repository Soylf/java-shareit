package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder()
                .name("test")
                .email("test@email.com")
                .build());

        userRepository.save(User.builder()
                .name("test1")
                .email("test2@email.com")
                .build());
    }

}
