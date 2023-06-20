package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentException;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long id, Long userId);

    ItemDto updateById(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> getAllByUserId(Long userId);

    List<ItemDto> searchByText(String text, Long userId);

    CommentDto createComment(CommentDto dto, Long itemId, Long userId) throws CommentException;
}
