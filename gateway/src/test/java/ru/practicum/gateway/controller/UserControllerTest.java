package ru.practicum.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.gateway.user.UserClient;
import ru.practicum.gateway.user.UserController;
import ru.practicum.gateway.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserClient userClient;


    UserDto userDto = new UserDto(1L, "user", "user@user.com");

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void shouldCreate() throws Exception {
        String jsonUser = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetUserById() throws Exception {
        Integer idUser = 1;
        mockMvc.perform(get("/users/{id}", idUser))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAll() throws Exception {
        mockMvc.perform(get("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldAddUserPostWhenFailName() throws Exception {
        UserDto user = UserDto.builder()
                .id(2L)
                .name("")
                .email("user@user.com")
                .build();

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldAdUserPostWhenFailEmail() throws Exception {
        UserDto user = new UserDto(2L, "user", "");
        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void shouldUpdatePatchUserWhenStatus200() throws Exception {
        UserDto user = new UserDto(1L, "update", "update@user.com");
        String jsonUser = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdatePatchUserName() throws Exception {
        String jsonUser = "{\"name\":\"updateName\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdatePatchUserEmail() throws Exception {
        String jsonUser = "{\"email\":\"updateName@user.com\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteUserNegativeUser() throws Exception {
        mockMvc.perform(delete("/users/-1"))
                .andExpect(status().is4xxClientError());
    }
}