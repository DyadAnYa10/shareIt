package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Sql(value = {"/test-schema.sql", "/users-create-test.sql", "/item-create-test.sql", "/all-bookings-create-test.sql"})
class BookingRepoTest {
    @Autowired
    private BookingRepository bookingRepository;
    private final int from = 0;
    private final int size = 10;
    private final long bookerId = 2L;
    private final long ownerId = 1L;
    private final Pageable pageable = PageRequest.of(
            from,
            size,
            Sort.by(Sort.Direction.DESC, "start")
    );

    @Test
    void findAllByBookerTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable);
        Assertions.assertThat(bookings).hasSize(3);
    }

    @Test
    void findAllWaitingByBookerTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
        Assertions.assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllRejectedByBookerTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable);
        Assertions.assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllPastByBookerTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllFutureByBookerTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(bookerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).hasSize(2);
    }

    @Test
    void findAllCurrentByBookerTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdCurrent(bookerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).isEmpty();
    }

    @Test
    void findAllByOwnerTest() {
        List<Booking> bookings = bookingRepository.getAllBookingsByOwner(ownerId, pageable);
        Assertions.assertThat(bookings).hasSize(3);
    }

    @Test
    void findAllWaitingByOwnerTest() {
        List<Booking> bookings = bookingRepository
                .findBookingByItemOwnerAndStatus(ownerId, BookingStatus.WAITING, pageable);
        Assertions.assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllRejectedByOwnerTest() {
        List<Booking> bookings = bookingRepository
                .findBookingByItemOwnerAndStatus(ownerId, BookingStatus.REJECTED, pageable);
        Assertions.assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllPastByOwnerTest() {
        List<Booking> bookings = bookingRepository
                .findBookingByItemOwnerAndEndIsBefore(ownerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).hasSize(1);
    }

    @Test
    void findAllFutureByOwnerTest() {
        List<Booking> bookings = bookingRepository
                .findBookingByItemOwnerAndStartIsAfter(ownerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).hasSize(2);
    }

    @Test
    void findAllCurrentByOwnerTest() {
        List<Booking> bookings = bookingRepository
                .findBookingsByItemOwnerCurrent(ownerId, LocalDateTime.now(), pageable);
        Assertions.assertThat(bookings).isEmpty();
    }
}
