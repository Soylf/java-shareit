package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    public CommentResponseDto toResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public CommentResponseDto fromComment(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentResponseDto> toListComment(List<Comment> comments) {
        return comments.stream()
                .map(this::fromComment)
                .collect(Collectors.toList());
    }

    public Comment fromCommentTo(CommentResponseDto commentDto, User user, Item item) {
        return Comment.builder()
                .item(item)
                .author(user)
                .text(commentDto.getText())
                .build();
    }
}
