package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonTest
class BookingGetDtoTest {
    @Autowired
    private JacksonTester<BookingGetDto> json;

    @Test
    @SneakyThrows
    void bookingDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(1);
        BookingGetDto bookingGetDto = BookingGetDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(new ItemDto(1L, "itemName", "description", true, null, null, null, null, null))
                .booker(new UserDto(1L, "userName", "dfgh@bk.ru"))
                .status(BookingStatus.APPROVED)
                .build();

        Optional<JsonContent<BookingGetDto>> result = Optional.of(json.write(bookingGetDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.start");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.end");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
                    Assertions.assertThat(i)
                            .hasJsonPathValue("$.item")
                            .extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .hasJsonPathValue("$.item")
                            .extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
                    Assertions.assertThat(i)
                            .hasJsonPathValue("$.booker")
                            .extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .hasJsonPathValue("$.booker")
                            .extractingJsonPathStringValue("$.booker.name").isEqualTo("userName");
                });
    }
}
