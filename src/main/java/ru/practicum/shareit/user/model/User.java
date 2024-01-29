package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NonNull
public class User {
    private Long id;
    private String name;
    private String email;
}
