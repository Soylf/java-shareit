package ru.practicum.shareit.item.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@Builder
@NonNull
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    @Column(name = "is_available")
    private Boolean available;
}
