package ru.practicum.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.booking.BookingClient;
import ru.practicum.gateway.booking.BookingController;
import ru.practicum.gateway.booking.dto.BookingGetDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private BookingGetDto bookingDto;
    LocalDateTime start;
    LocalDateTime end;
    @MockBean
    private BookingClient bookingClient;

    @BeforeEach
    public void setUp() throws Exception {

        start = LocalDateTime.now().plusMinutes(1).withNano(000);
        end = start.plusDays(1).withNano(000);

        bookingDto = new BookingGetDto(1L, 1L, start, end, null);
    }

    @Test
    public void shouldCreateBooking() throws Exception {
        Long bookerId = 1L;

        String jsonBooking = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBooking))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetBookingsById() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;


        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBookingsUpdateUser() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBookingsUpdateWithApprovedFalse() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(patch("/bookings/{bookingId}?approved=false", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldBookingsAllReservation() throws Throwable {
        Integer userId = 2;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBookingsAllReservationOwner() throws Throwable {
        Integer userId = 2;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBookingsWithoutSizeMinus() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?size=-1&from=0", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldBookingsWithoutFromMinus() throws Exception {
        Integer bookingId = 1;
        Integer userId = 1;

        mockMvc.perform(get("/bookings/owner?size=10&from=-1", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}