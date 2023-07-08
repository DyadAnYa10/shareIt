package ru.practicum.server.booking;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.server.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void bookingIncomeDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        String jsonString = "{\"itemId\": 2, \"start\": \"" + start + "\", \"end\": \"" + end + "\"}";

        Optional<BookingDto> dto = Optional.of(json.parseObject(jsonString));

        Assertions.assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("itemId", 2L);
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("start", start);
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("end", end);
                });
    }
}
