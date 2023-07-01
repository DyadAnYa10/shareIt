package ru.practicum.shareit.booking.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    private final RequestService requestService;

    @Override
    @Transactional
    public BookingGetDto createBooking(BookingDto bookingDto, long userId)
            throws UserConflictException, BookingCreateException {
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow();
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
            Booking booking = BookingMapper.toBooking(bookingDto, item, user);
            bookingCreate = bookingRepository.save(booking);
            log.info("Создано бронирование {}", bookingCreate.getId());
        } catch (Exception e) {
            log.error("Ошибка создания бронирования");
            e.printStackTrace();
        }
        return BookingMapper.toBookingGetDto(bookingCreate);
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
        return BookingMapper.toBookingGetDto(bookingRepository.save(booking.get()));
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
        return BookingMapper.toBookingGetDto(booking.get());
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByUser(int from, int size, String state, long userId) throws BookingStateException {
        userService.findUserById(userId);
        BookingState bookingState;
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Ошибка пагинации");
        }
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.DESC, "start")
        );
        try {
            bookingState = BookingState.valueOf(BookingState.class, state);
        } catch (Exception e) {
            log.error("Unknown state: " + state);
            throw new BookingStateException(state);
        }
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return bookingList.stream().map(BookingMapper::toBookingGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByOwner(int from, int size, String state, long userId) throws ItemExistsException {
        userService.findUserById(userId);
        BookingState bookingState;
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Ошибка пагинации");
        }
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.DESC, "start")
        );

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
                bookingList = bookingRepository.getAllBookingsByOwner(userId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository
                        .findBookingByItemOwnerAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository
                        .findBookingByItemOwnerAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findBookingsByItemOwnerCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingList = bookingRepository
                        .findBookingByItemOwnerAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .findBookingByItemOwnerAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
        }
        return bookingList.stream().map(BookingMapper::toBookingGetDto)
                .collect(Collectors.toList());
    }
}
