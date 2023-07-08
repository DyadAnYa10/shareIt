package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestIncomeDto;
import ru.practicum.server.request.dto.ItemRequestLongDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class ItemRequestController {
    private final RequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestIncomeDto requestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestLongDto> getForOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getForOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestLongDto> getAll(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestLongDto getById(@PathVariable("requestId") long requestId,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getById(requestId, userId);
    }
}