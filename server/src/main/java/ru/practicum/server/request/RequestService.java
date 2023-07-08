package ru.practicum.server.request;

import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestIncomeDto;
import ru.practicum.server.request.dto.ItemRequestLongDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(ItemRequestIncomeDto requestDto, long userId);

    List<ItemRequestLongDto> getAll(int from, int size, long userId);

    ItemRequestLongDto getById(long requestId, long userId);

    List<ItemRequestLongDto> getForOwner(long userId);
}
