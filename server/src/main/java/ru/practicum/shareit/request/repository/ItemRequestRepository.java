package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterOrderByCreatedAsc(User user);

    Page<ItemRequest> findAllByRequester_IdNotOrderByCreatedDesc(long userId, PageRequest of);

    List<ItemRequest> findAllByRequesterId(long l);
}
