package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingGetItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), item, user, BookingStatus.WAITING);
    }

    public BookingGetDto toBookingGetDto(Booking booking) {
        return new BookingGetDto(booking.getId(), booking.getStart(), booking.getEnd(), ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()), booking.getStatus());
    }

    public static BookingGetItemDto toBookingGetItemDto(Booking booking) {
        if (booking != null) {
            return new BookingGetItemDto(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
        return null;
    }
}
