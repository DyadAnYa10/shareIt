package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.exception.BookingCreateException;
import ru.practicum.shareit.booking.exception.BookingExistsException;
import ru.practicum.shareit.booking.exception.BookingStateException;
import ru.practicum.shareit.booking.exception.BookingStatusUpdateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exception.ItemExistsException;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserExistsException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private final BookingService bookingService;

    @Test
    @Order(0)
    @Sql(value = { "/test-schema.sql", "/users-create-test.sql", "/item-create-test.sql" })
    @SneakyThrows
    void createTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.createBooking(incomeDto, 2L));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.WAITING);
                });
    }

    @Test
    @Order(1)
    @SneakyThrows
    void confirmTest() {
        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.changeStatusOfBookingByOwner(1L, 1L, true));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
                });
    }

    @Test
    @Order(2)
    @SneakyThrows
    void getByIdTest() {
        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.getBooking(1L, 2L));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.APPROVED);
                });
    }

    @Test
    @Order(3)
    void getByIdFailTest() {
        assertThrows(UserExistsException.class, () -> bookingService.getBooking(1L, 3L));
    }

    @Test
    @Order(4)
    void rejectFailTest() {
        assertThrows(BookingStatusUpdateException.class, () -> bookingService.changeStatusOfBookingByOwner(1L, 1L, true));
    }

    @Test
    @Order(5)
    void getByIdNotExistTest() {
        assertThrows(BookingExistsException.class, () -> bookingService.getBooking(10L, 1L));
    }

    @Test
    @Order(6)
    void createFailTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(BookingCreateException.class, () -> bookingService.createBooking(incomeDto, 2L));
    }

    @Test
    @Order(7)
    @SneakyThrows
    void rejectTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(incomeDto, 2L);

        Optional<BookingGetDto> bookingDto = Optional.of(bookingService.changeStatusOfBookingByOwner(2L, 1L, false));

        assertThat(bookingDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrProperty("item");
                    assertThat(i.getItem()).hasFieldOrPropertyWithValue("id", 3L);
                    assertThat(i).hasFieldOrProperty("booker");
                    assertThat(i.getBooker()).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(i).hasFieldOrPropertyWithValue("status", BookingStatus.REJECTED);
                });
    }

    @Test
    @Order(8)
    void createWithNotExistItemTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(100L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NoSuchElementException.class, () -> bookingService.createBooking(incomeDto, 2L));
    }

    @Test
    @Order(8)
    void createWithNotExistUserTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NoSuchElementException.class, () -> bookingService.createBooking(incomeDto, 200L));
    }

    @Test
    @Order(9)
    @Sql(value = { "/all-bookings-create-test.sql" })
    void confirmNotByOwnerTest() {
        assertThrows(UserConflictException.class, () -> bookingService.changeStatusOfBookingByOwner(3L, 2L, true));
    }

    @Test
    @Order(10)
    void createFromUserTest() {
        BookingDto incomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(UserConflictException.class, () -> bookingService.createBooking(incomeDto, 1L));
    }

    @Test
    @Order(11)
    void getAllByOwnerTest() throws ItemExistsException, BookingStateException {
        int from = 0;
        int size = 10;
        BookingState state = BookingState.ALL;
        long userId = 1L;
        List<BookingGetDto> bookings = bookingService.getAllBookingsByOwner(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(5);

        state = BookingState.CURRENT;
        bookings = bookingService.getAllBookingsByOwner(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .isEmpty();

        state = BookingState.PAST;
        bookings = bookingService.getAllBookingsByOwner(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(1);

        state = BookingState.FUTURE;
        bookings = bookingService.getAllBookingsByOwner(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(4);

        state = BookingState.WAITING;
        bookings = bookingService.getAllBookingsByOwner(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(1);

        state = BookingState.REJECTED;
        bookings = bookingService.getAllBookingsByOwner(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(2);
    }

    @Test
    @Order(11)
    void getAllByBookerTest() throws UserExistsException, BookingStateException {
        int from = 0;
        int size = 10;
        BookingState state = BookingState.ALL;
        long userId = 2L;
        List<BookingGetDto> bookings = bookingService.getAllBookingsByUser(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(5);

        state = BookingState.CURRENT;
        bookings = bookingService.getAllBookingsByUser(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .isEmpty();

        state = BookingState.PAST;
        bookings = bookingService.getAllBookingsByUser(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(1);

        state = BookingState.FUTURE;
        bookings = bookingService.getAllBookingsByUser(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(4);

        state = BookingState.WAITING;
        bookings = bookingService.getAllBookingsByUser(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(1);

        state = BookingState.REJECTED;
        bookings = bookingService.getAllBookingsByUser(from, size, String.valueOf(state), userId);
        Assertions.assertThat(bookings)
                .hasSize(2);
    }
}
