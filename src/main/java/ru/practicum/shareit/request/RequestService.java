package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(ItemRequestIncomeDto requestDto, long userId);

    List<ItemRequestLongDto> getAll(int from, int size, long userId);

    ItemRequestLongDto getById(long requestId, long userId);

    List<ItemRequestLongDto> getForOwner(long userId);
}
