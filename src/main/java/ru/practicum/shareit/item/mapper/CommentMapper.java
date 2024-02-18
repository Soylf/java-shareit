package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    public Comment fromDto(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(commentDto.getItem())
                .author(commentDto.getAuthor())
                .created(commentDto.getCreated())
                .build();
    }

    public CommentDto fromComment(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentDto> toListComment(List<Comment> comments) {
        return comments.stream()
                .map(this::fromComment)
                .collect(Collectors.toList());
    }
}
