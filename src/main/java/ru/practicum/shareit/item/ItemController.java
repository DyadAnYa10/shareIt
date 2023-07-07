package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String X_HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size,
                                        @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return itemService.getAllByUserId(from, size, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader(X_HEADER_USER_ID) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(X_HEADER_USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateById(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        return itemService.searchByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto comment, @RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) throws CommentException {
        return itemService.createComment(comment, itemId, userId);
    }
}

