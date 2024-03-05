package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;
    @NotBlank(message = "Неверное название")
    private String name;
    @NotBlank
    @Size(max = 512, message = "Описание не должно превышать 512 символов")
    private String description;
    @NotNull(message = "available не может быть пустым")
    private Boolean available;
    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;
    private List<CommentResponseDto> comments;
    private Long requestId;
}
