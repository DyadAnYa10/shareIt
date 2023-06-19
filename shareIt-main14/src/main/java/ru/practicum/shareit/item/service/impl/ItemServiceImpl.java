package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.exception.OwnerItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userService.findUserById(userId);

        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Item with id='%s' not found", id)));
        List<Comment> comments = commentRepository.findByItemId(id);


        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            return constructItemDtoForOwner(item, now, CommentMapper.toDtoList(comments));
        }
        return ItemMapper.toDto(item, null, null, CommentMapper.toDtoList(comments));
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
        List<Comment> comments = commentRepository.findByItemId(id);
        itemRepository.save(foundItem);
        return ItemMapper.toDto(foundItem, CommentMapper.toDtoList(comments));
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        User user = userService.findUserById(userId);
        List<Item> items = itemRepository.getAllItemsByUser(user.getId());
        List<ItemDto> result = new ArrayList<>();
        fillItemDtoList(result, items, userId);

        result.sort((o1, o2) -> {
            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
                return o1.getId().compareTo(o2.getId());
            }
            if (o1.getNextBooking() != null && o2.getNextBooking() == null) {
                return -1;
            }
            if (o1.getNextBooking() == null && o2.getNextBooking() != null) {
                return 1;
            }
            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
                return -1;
            }
            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
                return 1;
            }
            return 0;
        });
        return result;
    }

    public List<ItemDto> searchByText(String text, Long userId) {
        if (!StringUtils.hasLength(text)) {
            return Collections.emptyList();
        }
        fillItemDtoList(new ArrayList<>(), itemRepository.searchItemsByText(text), userId);
        return ItemMapper.toItemDtoList(itemRepository.searchItemsByText(text));
    }

    private void fillItemDtoList(List<ItemDto> targetList, List<Item> foundItems, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        for (Item item : foundItems) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            List<CommentDto> commentDtoList = CommentMapper.toDtoList(comments);
            if (item.getOwner().getId().equals(userId)) {
                ItemDto dto = constructItemDtoForOwner(item, now, commentDtoList);
                targetList.add(dto);
            } else {
                targetList.add(ItemMapper.toDto(item, commentDtoList));
            }
        }
    }

    private ItemDto constructItemDtoForOwner(Item item, LocalDateTime now, List<CommentDto> comments) {
        Booking lastBooking = bookingRepository.findBookingByItemIdAndEndBefore(item.getId(), now,
                        BookingStatus.REJECTED)
                .stream().findFirst()
                .orElse(bookingRepository.findBookingsByItemOwnerCurrent(item.getOwner().getId(), now)
                        .stream().findFirst().orElse(null));
        List<Booking> nextBookings = bookingRepository.findBookingByItemIdAndStartAfter(item.getId(), now,
                BookingStatus.REJECTED);

        return ItemMapper.toDto(item,
                lastBooking,
                nextBookings.size() > 0 ? nextBookings.get(nextBookings.size() - 1) : null,
                comments);
    }

    @Override
    public CommentDto createComment(CommentDto dto, Long itemId, Long userId) throws CommentException {
        if (dto.getText().isBlank())
            throw new CommentException("Ошибка: пустой комментарий");
        Item item = itemRepository.findById(itemId).orElseThrow();
        User author = userService.findUserById(userId);

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException(" Ошибка: комментарий для itemId: " + itemId);
        }
        Comment comment = CommentMapper.toModel(dto, item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }
}
