package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Optional;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void toCommentDtoTest() {
        String jsonString = "{\"text\": \"Comment for item 1\"}";

        Optional<CommentDto> dto = Optional.of(json.parseObject(jsonString));

        Assertions.assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> Assertions.assertThat(i)
                        .hasFieldOrPropertyWithValue("text", "Comment for item 1"));
    }

    @Test
    @SneakyThrows
    void fromCommentDtoTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .build();

        Optional<JsonContent<CommentDto>> result = Optional.of(json.write(commentDto));

        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathNumberValue("$.id").isEqualTo(1);
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.text").isEqualTo("text");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.authorName").isEqualTo("name");
                });
    }
}
