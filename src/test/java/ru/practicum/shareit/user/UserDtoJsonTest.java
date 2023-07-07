package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

@JsonTest
class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void userDtoTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("user@yandex.ru")
                .build();

        Optional<JsonContent<UserDto>> result = Optional.of(json.write(userDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.id")
                            .isEqualTo(1);
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.name")
                            .isEqualTo("name");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("email")
                            .isEqualTo("user@yandex.ru");
                });
    }
}
