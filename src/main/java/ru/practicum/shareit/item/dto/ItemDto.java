package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @NotNull(message = "userId не может быть пустым")
    private Long ownerId;
    @NotBlank(message = "Неверное название")
    private String name;
    @NotBlank(message = "Неверное описание")
    private String description;
    @NotNull(message = "available не может быть пустым")
    private Boolean available;
    private LocalDateTime lastBookingDate;
    private LocalDateTime nextBookingDate;
    private List<CommentDto> commentDos;
}
