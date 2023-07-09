package ru.practicum.server.item;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.server.item.dto.ItemDto;

import java.util.Optional;

@JsonTest
class ItemIncomeDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void itemIncomeDtoTest() {
        String jsonString = "{\"name\": \"Дрель\", \"description\": \"Простая дрель\"," +
                "\"available\": true}";

        Optional<ItemDto> dto = Optional.of(json.parseObject(jsonString));

        Assertions.assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("name", "Дрель");
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("description", "Простая дрель");
                    Assertions.assertThat(i)
                            .hasFieldOrPropertyWithValue("available", true);
                });
    }
}
