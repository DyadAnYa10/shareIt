package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.exception.BookingCreateException;
import ru.practicum.shareit.booking.exception.BookingExistsException;
import ru.practicum.shareit.booking.exception.BookingStateException;
import ru.practicum.shareit.booking.exception.BookingStatusUpdateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.item.exception.ItemExistsException;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserExistsException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingGetDto createBooking(@Validated @RequestBody BookingDto bookingDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId)
            throws UserExistsException, ItemExistsException, UserConflictException, BookingCreateException {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingGetDto changeStatusOfBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable long bookingId,
                                                      @RequestParam("approved") boolean approved)
            throws UserExistsException, BookingExistsException, UserConflictException, BookingStatusUpdateException {
        return bookingService.changeStatusOfBookingByOwner(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingGetDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long bookingId) throws UserExistsException, BookingExistsException {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingGetDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state)
            throws UserExistsException, BookingStateException {
        return bookingService.getAllBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingGetDto> getAllBookingsByUserError(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(value = "state", defaultValue = "ALL") String state)
            throws ItemExistsException, BookingStateException {
        return bookingService.getAllBookingsByOwner(state, userId);
    }
}
