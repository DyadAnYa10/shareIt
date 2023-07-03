package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                //.owner(item.getOwner())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .owner(owner)
                .available(itemDto.getAvailable())
                .build();
    }
//
//    public static Item toItem(ItemDto itemDto, ItemRequest itemRequest) {
//        return Item.builder()
//                .name(itemDto.getName())
//                .id(itemDto.getId())
//                .description(itemDto.getDescription())
//                .owner(itemDto.getOwner())
//                .available(itemDto.getAvailable())
//                .itemRequest(itemRequest)
//                .build();
//    }
//
//    public static List<ItemDto> toItemDtoList(List<Item> items) {
//        return items.stream()
//                .map(ItemMapper::toItemDto)
//                .collect(Collectors.toList());
//    }

    public static ItemDto toDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                //.owner(item.getOwner())
                .lastBooking(BookingMapper.toBookingGetItemDto(lastBooking))
                .nextBooking(BookingMapper.toBookingGetItemDto(nextBooking))
                .comments(comments)
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public static ItemDto toDto(Item item, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }
}

