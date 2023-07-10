package ru.practicum.server.request.impl;

import lombok.experimental.UtilityClass;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestLongDto;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requestorId(itemRequestDto.getRequestorId())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestorId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestLongDto.ShortItemResponseDto toShortItemResponseDto(Item item) {
        return ItemRequestLongDto.ShortItemResponseDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public static ItemRequestLongDto toItemRequestDtoForOwner(ItemRequest itemRequest,
                                                              List<Item> items) {
        return ItemRequestLongDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(
                        items.stream()
                                .map(ItemRequestMapper::toShortItemResponseDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static List<ItemRequestLongDto> toItemRequestDtoForOwner(List<ItemRequest> itemRequests,
                                                                    Map<ItemRequest, List<Item>> itemsByRequests) {
        return itemRequests.stream()
                .map(itemRequest -> toItemRequestDtoForOwner(
                        itemRequest,
                        itemsByRequests.getOrDefault(itemRequest, List.of())
                ))
                .collect(Collectors.toList());
    }
}
