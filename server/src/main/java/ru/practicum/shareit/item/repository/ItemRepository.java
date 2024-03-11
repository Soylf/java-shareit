package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE LOWER(i.name) LIKE CONCAT('%', LOWER(?1), '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', LOWER(?1), '%') " +
            "AND i.available = true")
    Page<Item> search(String text, Pageable pageable);

    Page<Item> findItemsByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);


    List<Item> findAllByRequest(ItemRequest requestId);
}
