package ru.practicum.server.request.user;

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
import ru.practicum.server.user.UserController;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final UserService userService;

    @SneakyThrows
    @Test
    void getById_correctUser_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getAll_correctUser_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(get("/users", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void createTest_correctUserIncomeDto_thenReturnOk() {
        UserDto userIncomeDto = UserDto.builder()
                .name("User")
                .email("user@yandex.ru")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();

        when(userService.create(any())).thenReturn(userDto);
        String result = mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userIncomeDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);
    }


    @SneakyThrows
    @Test
    void updateTest_correctUserIncomeDto_thenReturnOk() {
        UserDto userIncomeDto = UserDto.builder()
                .name("User")
                .email("user@yandex.ru")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("UserUpdate")
                .email("user@yandex.ru")
                .build();

        when(userService.updateUser(any(), anyLong())).thenReturn(userDto);
        String result = mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userIncomeDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void deleteById_correctUser_thenReturnOk() {
        long userId = 1L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUserById(userId);
    }

}
