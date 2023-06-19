package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.BookingCreateException;
import ru.practicum.shareit.booking.exception.BookingExistsException;
import ru.practicum.shareit.booking.exception.BookingStateException;
import ru.practicum.shareit.booking.exception.BookingStatusUpdateException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.item.exception.ItemExistsException;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserExistsException;

import java.util.List;

public interface BookingService {
    BookingGetDto createBooking(BookingDto bookingDto, long userId) throws UserExistsException, ItemExistsException, UserConflictException, BookingCreateException;

    BookingGetDto changeStatusOfBookingByOwner(long bookingId, long userId, boolean approved) throws UserExistsException, UserConflictException, BookingExistsException, BookingStatusUpdateException;

    BookingGetDto getBooking(long bookingId, long userId) throws UserExistsException, BookingExistsException;

    List<BookingGetDto> getAllBookingsByUser(String state, long userId) throws UserExistsException, BookingStateException;

    List<BookingGetDto> getAllBookingsByOwner(String state, long userId) throws ItemExistsException, BookingStateException;
}
