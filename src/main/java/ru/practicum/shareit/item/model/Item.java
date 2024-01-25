package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Data
@NonNull
public class Item {
    private Integer id;
    private String name;
    private String description;
    private boolean available;
}
