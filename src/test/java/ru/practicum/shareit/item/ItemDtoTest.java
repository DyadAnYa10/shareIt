package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingGetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void itemDtoTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("text")
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .name("user")
                        .email("user@yandex.ru")
                        .build())
                .lastBooking(new BookingGetItemDto(1L, 2L))
                .nextBooking(new BookingGetItemDto(2L, 2L))
                .comments(List.of())
                .requestId(1L)
                .build();

        Optional<JsonContent<ItemDto>> result = Optional.of(json.write(itemDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.name").isEqualTo("name");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.description").isEqualTo("text");
                    Assertions.assertThat(i)
                            .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
                    Assertions.assertThat(i)
                            .hasJsonPathValue("owner");
                    Assertions.assertThat(i)
                            .hasJsonPathValue("lastBooking");
                    Assertions.assertThat(i)
                            .hasJsonPathValue("nextBooking");
                    Assertions.assertThat(i)
                            .hasJsonPathArrayValue("comments");
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
                });
    }
}
