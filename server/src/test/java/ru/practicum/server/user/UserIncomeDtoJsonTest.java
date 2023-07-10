package ru.practicum.server.user;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.server.user.dto.UserDto;

import java.util.Optional;

@JsonTest
class UserIncomeDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void userIncomeDtoTest() {
        String jsonString = "{\"name\": \"user\", \"email\": \"user@user.com\"}";

        Optional<UserDto> dto = Optional.of(json.parseObject(jsonString));

        Assertions.assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("name", "user");
                    Assertions.assertThat(i).hasFieldOrPropertyWithValue("email", "user@user.com");
                });
    }
}
