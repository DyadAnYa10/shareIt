package ru.practicum.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingGetDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.booking.service.BookingServiceImpl;
import ru.practicum.server.exception.BookingStateException;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.ValidateBookingException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingMockServiceTest {
    public static final LocalDateTime DATE = LocalDateTime.now();

    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingDto inputDto;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        inputDto = BookingDto.builder()
                .itemId(5L)
                .start(DATE)
                .end(DATE.plusDays(10))
                .build();

        user = User.builder()
                .id(2L)
                .name("name")
                .email("user@email.com")
                .build();

        owner = User.builder()
                .id(3L)
                .name("owner")
                .email("user2@email.ru")
                .build();

        item = Item.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(DATE)
                .end(DATE.plusDays(10))
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBookingTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingGetDto result = bookingService.createBooking(inputDto, 2L);

        assertNotNull(result);
        assertEquals(inputDto.getItemId(), result.getItem().getId());
        assertEquals(inputDto.getStart(), result.getStart());
        assertEquals(inputDto.getEnd(), result.getEnd());
    }

    @Test
    void createBookingWithValidateExceptionTest() {
        item.setAvailable(false);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ValidateBookingException e = assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.createBooking(inputDto, 2L);
                });
        assertNotNull(e);
    }

    @Test
    void createBookingWithNotFoundExceptionTest() {
        item.setOwner(user);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.createBooking(inputDto, 2L);
                });
        assertNotNull(e);
    }

    @Test
    void updateBookingTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingGetDto result = bookingService.changeStatusOfBookingByOwner(1L, 3L, true);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void updateBookingWithNotFoundExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);
        item.setOwner(user);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.changeStatusOfBookingByOwner(1L, 2L, true);
                });
        assertNotNull(e);
    }

    @Test
    void updateBookingWithValidateExceptionTest() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);

        ValidateBookingException e = assertThrows(ValidateBookingException.class,
                () -> {
                    bookingService.changeStatusOfBookingByOwner(1L, 3L, true);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingByIdTest() {
        item.setOwner(owner);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingGetDto result = bookingService.getBooking(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findBookingByIdWithNotFoundExceptionTest() {
        user.setId(11L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getBooking(1L, 1L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateRejectedTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        BookingStateException e = assertThrows(BookingStateException.class,
                () -> {
                    List<BookingGetDto> result = bookingService
                            .getAllBookingsByUser(0, 10, "rejected", 2L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateWaitingTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        BookingStateException e = assertThrows(BookingStateException.class,
                () -> {
                    List<BookingGetDto> result = bookingService
                            .getAllBookingsByUser(0, 10, "waiting", 2L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateCurrentTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        BookingStateException e = assertThrows(BookingStateException.class,
                () -> {
                    List<BookingGetDto> result = bookingService
                            .getAllBookingsByUser(0, 10, "current", 2L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateFutureTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        BookingStateException e = assertThrows(BookingStateException.class,
                () -> {
                    List<BookingGetDto> result = bookingService
                            .getAllBookingsByUser(0, 10, "future", 2L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStatePastTest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        BookingStateException e = assertThrows(BookingStateException.class,
                () -> {
                    List<BookingGetDto> result = bookingService
                            .getAllBookingsByUser(0, 10, "past", 2L);
                });
        assertNotNull(e);
    }

    @Test
    void findBookingsByUserStateAllTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository.findByBookerIdOrderByStartDesc(any(), any()))
                .thenReturn((List.of(booking)));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByUser(0, 10, "ALL", 2L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateRejectedTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndStatus(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByOwner(0, 10, "REJECTED", 3L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateWaitingTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndStatus(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByOwner(0, 10, "WAITING", 3L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateCurrentTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingsByItemOwnerCurrent(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByOwner(0, 10, "CURRENT", 3L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateFutureTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndStartIsAfter(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByOwner(0, 10, "FUTURE", 3L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStatePastTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        when(bookingRepository.findBookingByItemOwnerAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByOwner(0, 10, "PAST", 3L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findBookingsByItemsOwnerStateAllTest() throws BookingStateException {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.singletonList(item));

        when(bookingRepository.getAllBookingsByOwner(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingGetDto> result = bookingService
                .getAllBookingsByOwner(0, 10, "ALL", 3L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
