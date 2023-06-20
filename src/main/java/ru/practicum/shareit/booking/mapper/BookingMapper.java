package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingGetItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, Item item, User user, BookingStatus bookingStatus) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), item, user, bookingStatus);
    }

    public BookingGetDto toBookingGetDto(Booking booking) {
        return new BookingGetDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(),
                booking.getBooker(), booking.getStatus());
    }

    public static BookingGetItemDto toBookingGetItemDto(Booking booking) {
        if (booking != null) {
            return new BookingGetItemDto(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
        return null;
    }
}
