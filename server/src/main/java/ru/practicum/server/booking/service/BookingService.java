package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingGetDto;
import ru.practicum.server.booking.exception.BookingCreateException;
import ru.practicum.server.booking.exception.BookingExistsException;
import ru.practicum.server.booking.exception.BookingStateException;
import ru.practicum.server.booking.exception.BookingStatusUpdateException;
import ru.practicum.server.item.exception.ItemExistsException;
import ru.practicum.server.user.exception.UserConflictException;
import ru.practicum.server.user.exception.UserExistsException;

import java.util.List;

public interface BookingService {
    BookingGetDto createBooking(BookingDto bookingDto, long userId) throws UserExistsException, ItemExistsException, UserConflictException, BookingCreateException;

    BookingGetDto changeStatusOfBookingByOwner(long bookingId, long userId, boolean approved) throws UserExistsException, UserConflictException, BookingExistsException, BookingStatusUpdateException;

    BookingGetDto getBooking(long bookingId, long userId) throws UserExistsException, BookingExistsException;

    List<BookingGetDto> getAllBookingsByUser(int from, int size, String state, long userId) throws UserExistsException, BookingStateException;

    List<BookingGetDto> getAllBookingsByOwner(int from, int size, String state, long userId) throws ItemExistsException, BookingStateException;
}
