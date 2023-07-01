package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class ItemRequestController {
    private final RequestService service;

    @PostMapping
    public ItemRequestDto create(@Validated(Create.class) @RequestBody ItemRequestIncomeDto requestDto,
                                 @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return service.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestLongDto> getForOwner(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return service.getForOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestLongDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size,
                                           @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return service.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestLongDto getById(@PathVariable("requestId") @Positive long requestId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        return service.getById(requestId, userId);
    }
}