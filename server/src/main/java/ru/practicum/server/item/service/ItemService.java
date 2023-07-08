package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.exception.CommentException;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long id, Long userId);

    ItemDto updateById(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> getAllByUserId(int from, int size, long userId);

    List<ItemDto> searchByText(String text, int from, int size);

    CommentDto createComment(CommentDto dto, Long itemId, Long userId) throws CommentException;
}
