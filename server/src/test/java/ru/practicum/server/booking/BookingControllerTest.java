package ru.practicum.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingGetDto;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void getById_correctUser_thenReturnOk() {
        long userId = 1L;
        long bookingId = 1L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getBooking(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getAllByBooker_withoutStateAndPagination_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsByUser(0, 10, BookingState.ALL.name(), 1L);
    }

    @SneakyThrows
    @Test
    void getAllByOwner_WAITING_thenReturnBadRequest() {
        long userId = 1L;
        BookingState state = BookingState.WAITING;
        mockMvc.perform(get("/bookings/owner?state={state}", state)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsByOwner(0, 10, BookingState.WAITING.name(), 1L);
    }

    @SneakyThrows
    @Test
    void create_allCorrect_thenReturnOk() {
        long userId = 1L;
        BookingDto bookingIncomeDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        BookingGetDto bookingDto = BookingGetDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .booker(new UserDto(1L, "user", "email@email.com"))
                .item(new ItemDto(1L, "item",  "item_description", true, null, null, null, null, null))
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDto);
        String content = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingIncomeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), content);
    }

    @SneakyThrows
    @Test
    void confirm_allCorrect_thenReturnOk() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).changeStatusOfBookingByOwner(bookingId, userId, approved);
    }

    @SneakyThrows
    @Test
    void getAllByOwner_UNSUPPORTED_STATUS_thenReturnBadRequest() {
        long userId = 1L;
        String state = "abracadabra";
        mockMvc.perform(get("/bookings/owner?state={state}", state)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print());

        verify(bookingService).getAllBookingsByOwner(0, 10, state, 1L);
    }
}
