package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder
@NonNull
public class ItemDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Boolean available;
    private Integer numTimes;
}
