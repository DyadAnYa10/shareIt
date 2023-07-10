package ru.practicum.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.BookingState;
import ru.practicum.server.booking.BookingStatus;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingGetDto;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.BookingStateException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidateBookingException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingGetDto createBooking(BookingDto bookingDto, long userId) {
        User user = checkUser(userId);
        Item item = checkItem(bookingDto.getItemId());
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidateBookingException("Ошибка создания бронирования: start = " + bookingDto.getStart() + ", " +
                    "end = " + bookingDto.getEnd());
        }
        if (item.getOwner().getId() == userId) {
            log.error("Ошибка создания бронирования user id = owner id = {}", userId);
            throw new NotFoundException("Ошибка создания бронирования user id = owner id = " + userId);
        }

        if (!item.getAvailable() || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.error("Ошибка создания бронирования");
            throw new ValidateBookingException("Ошибка создания бронирования");
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
    public BookingGetDto changeStatusOfBookingByOwner(long bookingId, long userId, boolean approved) {
        Booking booking = checkBooking(bookingId);
        Item item = checkItem(booking.getItem().getId());
        boolean trueOwner = item.getOwner().getId().equals(userId);
        if (!trueOwner) {
            throw new NotFoundException("Пользователь " + userId
                    + " не является владельцем вещи " + item.getId());
        }

        BookingStatus status = Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        boolean trueStatus = booking.getStatus().equals(status);

        if (trueStatus) {
            throw new ValidateBookingException("Бронирование " + bookingId
                    + "подтверждено, невозможно изменить статус");
        }
        booking.setStatus(status);
        return BookingMapper.toBookingGetDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingGetDto getBooking(long bookingId, long userId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        Long itemOwner = booking.getItem().getOwner().getId();
        Long bookingOwner = booking.getBooker().getId();
        boolean itemOrBookingOwner = userId == bookingOwner || userId == itemOwner;

        if (!itemOrBookingOwner) {
            log.warn("Пользователь с Id: {} не является владельцем предмета c Id: {} или брони c Id: {}",
                    userId, booking.getItem().getId(), booking.getId());
            throw new NotFoundException(
                    String.format("Пользователь с Id: %d не является владельцем предмета c Id: %d или брони c Id: %d",
                            userId, booking.getItem().getId(), booking.getId()));
        }
        return BookingMapper.toBookingGetDto(booking);
    }

    @Override
    @Transactional
    public List<BookingGetDto> getAllBookingsByUser(int from, int size, String state, long userId) throws BookingStateException {
        checkUser(userId);
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
    public List<BookingGetDto> getAllBookingsByOwner(int from, int size, String state, long userId) throws BookingStateException {
        userRepository.findById(userId).orElseThrow();
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
            throw new BookingStateException(state);
        }
        if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
            throw new NotFoundException("У пользователя нет вещей, id = " + userId);
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

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Не найден пользователь с id-{}: ", userId);
                    return new NotFoundException(String.format(
                            "Не найден пользователь с id: %d", userId));
                }
        );
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
                    log.warn("Не найден предмет с id-{}: ", itemId);
                    return new NotFoundException(String.format(
                            "Не найден предмет с id: %d", itemId));
                }
        );
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
                    log.warn("Не найдено бронирование с id-{}: ", bookingId);
                    return new NotFoundException(String.format(
                            "Не найдено бронирование с id: %d", bookingId));
                }
        );
    }
}
