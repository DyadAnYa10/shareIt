package ru.practicum.shareit.request.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

class UserMapperTest {

    @Test
    void toUserTest() {
        UserDto incomeDto = UserDto.builder()
                .name("userName")
                .email("user@mail.com")
                .build();

        User user = UserMapper.toUser(incomeDto);

        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("name", "userName")
                .hasFieldOrPropertyWithValue("email", "user@mail.com");
    }

    @Test
    void toUserDtoTest() {
        User user = fillEntity();

        UserDto userDto = UserMapper.toUserDto(user);

        Assertions.assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "name")
                .hasFieldOrPropertyWithValue("email", "user@mail.com");
    }

    @Test
    void toUserDtosTest() {
        List<User> users = List.of(fillEntity());

        List<UserDto> userDtos = UserMapper.toListUserDtos(users);

        Assertions.assertThat(userDtos)
                .hasSize(1);
    }

    private User fillEntity() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("user@mail.com");

        return user;
    }
}
