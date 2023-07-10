package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingGetDto;
import ru.practicum.server.exception.BookingStateException;


import java.util.List;

public interface BookingService {
    BookingGetDto createBooking(BookingDto bookingDto, long userId);

    BookingGetDto changeStatusOfBookingByOwner(long bookingId, long userId, boolean approved);

    BookingGetDto getBooking(long bookingId, long userId);

    List<BookingGetDto> getAllBookingsByUser(int from, int size, String state, long userId) throws BookingStateException;

    List<BookingGetDto> getAllBookingsByOwner(int from, int size, String state, long userId) throws BookingStateException;
}
