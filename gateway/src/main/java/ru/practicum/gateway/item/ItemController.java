package ru.practicum.gateway.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.CommentDTO;
import ru.practicum.gateway.item.dto.ItemDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestBody @Valid ItemDTO dto) {
        log.info("Получен запрос к эндпоинту /items create с headers {}", userId);
        return itemClient.createItem(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен запрос к эндпоинту: /items getAll с headers {}", userId);
        return itemClient.findAllItemByUserId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                               @PathVariable("id") @Positive Long itemId) {
        log.info("Получен запрос к эндпоинту: /items geById с id={}", itemId);
        return itemClient.findItemById(itemId, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody ItemDTO itemDto) {
        log.info("Получен запрос к эндпоинту: /items update с ItemId={} с headers {}", itemId, userId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByRequest(@RequestParam("text") String text,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        if (!StringUtils.hasText(text)) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Получен запрос к эндпоинту: items/search с text: {}", text);
        return itemClient.findItemsByRequest(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable("itemId") @Positive Long itemId,
                                             @Valid @RequestBody CommentDTO comment) {
        log.info("Получен запрос к эндпоинту /items{itemId}/comment addComment с headers {}, с itemId {}", userId, itemId);
        return itemClient.addComment(itemId, userId, comment);
    }
}
