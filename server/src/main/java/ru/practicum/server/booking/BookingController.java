package ru.practicum.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingGetDto;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exception.BookingStateException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingGetDto createBooking(@RequestBody BookingDto bookingDto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingGetDto changeStatusOfBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable long bookingId,
                                                      @RequestParam("approved") boolean approved) {
        return bookingService.changeStatusOfBookingByOwner(bookingId, userId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingGetDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingGetDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state) throws BookingStateException {
        return bookingService.getAllBookingsByUser(from, size, state, userId);
    }


    @GetMapping("/owner")
    public List<BookingGetDto> getAllBookingsByUserError(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size) throws BookingStateException {
        return bookingService.getAllBookingsByOwner(from, size, state, userId);
    }
}
