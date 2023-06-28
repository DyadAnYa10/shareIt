package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemExistsException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@Data
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingGetDto createBooking(BookingDto bookingDto, long userId)
            throws UserConflictException, BookingCreateException {
        User user = userService.findUserById(userId);
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId(), userId));
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BookingCreateException("Ошибка создания бронирования: start = " + bookingDto.getStart() + ", " +
                    "end = " + bookingDto.getEnd());
        }
        if (item.getOwner().getId() == userId) {
            log.error("Ошибка создания бронирования user id = owner id = {}", userId);
            throw new UserConflictException("Ошибка создания бронирования user id = owner id = " + userId);
        }

        if (!item.getAvailable() || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.error("Ошибка создания бронирования");
            throw new BookingCreateException("Ошибка создания бронирования");
        }
        Booking bookingCreate = new Booking();
        try {
            Booking booking = bookingMapper.toBooking(bookingDto, item, user, BookingStatus.WAITING);
            bookingCreate = bookingRepository.save(booking);
            log.info("Создано бронирование {}", bookingCreate.getId());
        } catch (Exception e) {
            log.error("Ошибка создания бронирования");
            e.printStackTrace();
        }
        return bookingMapper.toBookingGetDto(bookingCreate);
    }

    @Override
    @Transactional
    public BookingGetDto changeStatusOfBookingByOwner(long bookingId, long userId, boolean approved)
            throws UserConflictException, BookingExistsException, BookingStatusUpdateException {
        userService.findUserById(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingExistsException("Нет бронирования " + bookingId);
        }
        if (booking.get().getItem().getOwner().getId() != userId) {
            throw new UserConflictException("Пользователь " + userId
                    + " не является владельцем вещи " + booking.get().getItem().getId());
        }
        if (booking.get().getStatus().name().equals(BookingStatus.APPROVED.name())) {
            throw new BookingStatusUpdateException("Бронирование " + bookingId
                    + "подтверждено, невозможно изменить статус");
        }
        if (approved) {
            booking.get().setStatus(BookingStatus.APPROVED);
        } else {
            booking.get().setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toBookingGetDto(bookingRepository.save(booking.get()));
    }

    @Override
    @Transactional
    public BookingGetDto getBooking(long bookingId, long userId) throws UserExistsException, BookingExistsException {
        userService.findUserById(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingExistsException("Нет бронирования " + bookingId);
        }
        if (booking.get().getBooker().getId() != userId && booking.get().getItem().getOwner().getId() != userId) {
            throw new UserExistsException("Пользователь не является owner или booker id = " + userId);
        }
        return bookingMapper.toBookingGetDto(booking.get());
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByUser(String state, long userId) throws BookingStateException {
        userService.findUserById(userId);
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (Exception e) {
            log.error("Unknown state: " + state);
            throw new BookingStateException(state);
        }
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingList.stream().map(bookingMapper::toBookingGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByOwner(String state, long userId) throws ItemExistsException {
        userService.findUserById(userId);
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (IllegalArgumentException e) {
            throw new WrongStateException(state);
        }
        if (itemService.getAllByUserId(userId).size() < 1) {
            throw new ItemExistsException("У пользователя нет вещей, id = " + userId);
        }
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.getAllBookingsByOwner(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByItemOwnerAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .findBookingByItemOwnerAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByItemOwnerAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByItemOwnerAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingList.stream().map(bookingMapper::toBookingGetDto)
                .collect(Collectors.toList());
    }
}
