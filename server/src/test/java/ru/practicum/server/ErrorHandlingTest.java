package ru.practicum.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/test-schema.sql", "/exceptionHandler_setUp_test.sql"})
class ErrorHandlingTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @SneakyThrows
    @Test
    void entityNotExistExceptionTest() {
        long userId = 10L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void userNotValidExceptionTest() {
        long bookingId = 1L;
        long userId = 3L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateErrorExceptionTest() {
        long itemId = 1L;
        long userId = 3L;
        ItemDto incomeDto = ItemDto.builder()
                .name("item")
                .description("users 3 item")
                .available(true)
                .build();

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    void bookingPatchExceptionTest() {
        long bookingId = 1L;
        long userId = 3L;
        boolean approved = false;

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void unsupportedStatusExceptionTest() {
        long userId = 3L;
        String state = "abracadabra";

        mockMvc.perform(get("/bookings/owner/?state={state}", state)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void creationErrorExceptionTest() {
        long itemId = 1L;
        long userId = 3L;
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .authorName("Ivan")
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
