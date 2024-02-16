package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;
    private String text;
    private Item item;
    private User author;
    private ZonedDateTime created;
}
