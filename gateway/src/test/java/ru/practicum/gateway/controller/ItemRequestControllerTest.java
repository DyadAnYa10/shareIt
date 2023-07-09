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
import ru.practicum.gateway.itemrequest.ItemRequestClient;
import ru.practicum.gateway.itemrequest.ItemRequestController;
import ru.practicum.gateway.itemrequest.dto.ItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private ItemRequestDto itemRequestDto;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    public void setUp() throws Exception {
        itemRequestDto = new ItemRequestDto("Хотел бы воспользоваться щёткой для обуви");
    }

    @Test
    public void shouldItemRequestWithoutUser() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemRequestDto);
        Long userId = -99L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldItemRequestWithEmptyDescription() throws Exception {
        ItemRequestDto item = new ItemRequestDto(null);
        String jsonItem = objectMapper.writeValueAsString(item);
        Long userId = 1L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldGetItemRequestWithoutUser() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldGetItemRequestWithoutRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetItemRequestWithoutPaginationParams() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetItemRequestWithFrom0Size0() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=0&size=0")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithFromMinSize20() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=-1&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithFrom0SizeMin() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=0&size=-1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldGetItemRequestWithFrom0Size20() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all?from=0&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldItemRequestAdd() throws Exception {
        String jsonItem = objectMapper.writeValueAsString(itemRequestDto);
        Long userId = 1L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItem))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldItemRequestById() throws Exception {

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}