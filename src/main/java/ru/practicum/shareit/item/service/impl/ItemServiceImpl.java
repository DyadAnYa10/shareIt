package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.item.exception.OwnerItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userService.findUserById(userId);

        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id='%s' not found", id)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateById(ItemDto itemDto, Long id, Long userId) {
        Item foundItem = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id='%s' not found", id)));

        if (!Objects.equals(foundItem.getOwner().getId(), userId)) {
            throw new OwnerItemException(String.format("Owner of item with id='%s' is another", foundItem.getId()));
        }

        if (StringUtils.hasLength(itemDto.getName())) {
            foundItem.setName(itemDto.getName());
        }

        if (StringUtils.hasLength(itemDto.getDescription())) {
            foundItem.setDescription(itemDto.getDescription());
        }

        if ((itemDto.getAvailable() != null)) {
            foundItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.updateById(foundItem, id);
        return ItemMapper.toItemDto(foundItem);
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        List<Item> items = itemRepository.findByUserId(userId);

        return ItemMapper.toItemDtoList(items);
    }

    public List<ItemDto> searchByText(String text) {
        if (!StringUtils.hasLength(text)) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.findByText(text));
    }
}
