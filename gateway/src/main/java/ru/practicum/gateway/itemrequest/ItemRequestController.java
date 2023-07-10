package ru.practicum.gateway.itemrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.itemrequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту /requests create с headers {}", userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту /requests getForUser с headers {}", userId);
        return requestClient.findAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос к эндпоинту /requests getOtherUsers с headers {}, from{}, size{}", userId, from, size);
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable(name = "requestId") Long requestId) {
        log.info("Получен запрос к эндпоинту /requests getOtherUsers с headers {}, c requestId {}", userId, requestId);
        return requestClient.findById(userId, requestId);
    }
}
