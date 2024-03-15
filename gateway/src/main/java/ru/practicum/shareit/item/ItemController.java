package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentRequestDto commentDto) {
        return client.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        checkItem(itemDto);
        return client.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return client.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam("text") String text, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from, @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return client.searchItem(userId, text, from, size);
    }


    private void checkItem(ItemDto item) {
        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new BadRequestException("Name");
            }
        }
    }
}
