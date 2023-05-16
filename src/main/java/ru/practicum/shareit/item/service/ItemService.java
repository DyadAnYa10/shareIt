package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long id);

    ItemDto updateById(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> getAllByUserId(Long userId);

    List<ItemDto> searchByText(String text);
}
