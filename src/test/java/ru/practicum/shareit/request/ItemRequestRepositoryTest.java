package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .name("test")
                .email("test@mail.com")
                .build();
        User user = entityManager.persist(owner);

        User owner2 = User.builder()
                .name("test")
                .email("test2@mail.com")
                .build();
        User user1 = entityManager.persist(owner2);

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description("test")
                .build();

        entityManager.persist(itemRequest);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .requester(user1)
                .created(LocalDateTime.now())
                .description("test")
                .build();

        entityManager.persist(itemRequest1);
    }
}