package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.addItem(itemDto, userId);
    }

    @PostMapping("/items/{itemId}/comment")
    public boolean addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        service.addComment(commentDto,itemId,userId);
        return true;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItem(itemId,userId).orElse(null);
    }

    @GetMapping
    public List<ItemDto> getAllItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItem(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam("text") String text) {
        return service.searchItem(text);
    }
}