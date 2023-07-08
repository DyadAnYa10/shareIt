package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingRequestDTO;
import ru.practicum.gateway.exception.UnknownBookingException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new UnknownBookingException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingsByUser(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestBody @Valid BookingRequestDTO requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable("bookingId") Long bookingId,
                                                @RequestParam("approved") Boolean approved) {
        log.info("Получен запрос к эндпоинту /bookings updateStatus с headers {}, с bookingId {}, статус {}",
                userId, bookingId, approved);
        return bookingClient.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsByItemsOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                           @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен запрос к эндпоинту /bookings getAllReservation с state {}", stateParam);
        State state = State.from(stateParam)
                .orElseThrow(() -> new UnknownBookingException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingsByItemsOwner(userId, state, from, size);
    }
}