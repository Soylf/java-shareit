package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item,Long> {

    @Modifying
    @Query("UPDATE Item e SET " +
            "e.name = CASE WHEN :#{#item.name} IS NOT NULL THEN :#{#item.name} ELSE e.name END, " +
            "e.description = CASE WHEN :#{#item.description} IS NOT NULL THEN :#{#item.description} ELSE e.description END, " +
            "e.available = CASE WHEN :#{#item.available} IS NOT NULL THEN :#{#item.available} ELSE e.available END " +
            "WHERE e.id= :itemId AND e.userId = :userId")
    void updateItemFields(Item item, Long itemId, Long userId);
}
