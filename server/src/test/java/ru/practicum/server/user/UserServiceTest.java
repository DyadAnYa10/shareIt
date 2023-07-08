package ru.practicum.server.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;

    @Test
    @Order(0)
    @Sql(value = {"/src/test/resources/test-schema.sql"})
    void createTest() {
        UserDto userCreateDto = UserDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        Optional<UserDto> userDto = Optional.of(userService.create(userCreateDto));

        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "user");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "user@yandex.ru");
                        }
                );
    }

    @Test
    @Order(1)
    void updateTest() {
        UserDto userUpdateDto = UserDto.builder()
                .name("userUpdated")
                .email("userUpdated@yandex.ru")
                .build();
        Optional<UserDto> userDto = Optional.of(userService.updateUser(userUpdateDto, 1L));

        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "userUpdated");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "userUpdated@yandex.ru");
                        }
                );
    }

    @Test
    @Order(2)
    void getByIdCorrectTest() {
        Optional<UserDto> userDto = Optional.of(userService.getUserById(1L));

        assertThat(userDto)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "userUpdated");
                            assertThat(f).hasFieldOrPropertyWithValue("email", "userUpdated@yandex.ru");
                        }
                );
    }

    @Test
    @Order(3)
    void getByIdUnCorrectTest() {
        assertThrows(NoSuchElementException.class, () -> userService.getUserById(100L));
    }

    @Test
    @Order(4)
    void getAllTest() {
        UserDto userCreateDto = UserDto.builder()
                .name("user1")
                .email("user1@yandex.ru")
                .build();
        userService.create(userCreateDto);
        List<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .hasSize(2)
                .map(UserDto::getId)
                .contains(1L, 2L);
    }

    @Test
    @Order(5)
    void deleteByIdTest() {
        userService.deleteUserById(1L);
        List<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .hasSize(1)
                .map(UserDto::getId)
                .contains(2L);
    }

}
