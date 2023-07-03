package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomeDto;
import ru.practicum.shareit.request.dto.ItemRequestLongDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceTest {
    private final RequestService requestService;

    @Test
    @Order(0)
    @Sql(value = { "/test-schema.sql", "/users-create-test.sql" })
    void createTest() {
        long userId = 1L;
        ItemRequestIncomeDto incomeDto = ItemRequestIncomeDto.builder()
                .description("text")
                .build();

        Optional<ItemRequestDto> itemRequestDto = Optional.of(requestService.create(incomeDto, userId));

        assertThat(itemRequestDto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrPropertyWithValue("description", "text");
                    assertThat(i).hasFieldOrProperty("created");
                    assertThat(i.getCreated()).isNotNull();
                    assertThat(i).hasFieldOrProperty("requestor");
                    assertThat(i.getRequestor()).hasFieldOrPropertyWithValue("requestorId", 1L);
                    assertThat(i.getRequestor()).hasFieldOrPropertyWithValue("requestorName", "name");
                });
    }

    @Test
    @Order(1)
    @Sql(value = { "/item-for-request-create-test.sql" })
    void getById() {
        long requestId = 1L;
        long userId = 1L;

        Optional<ItemRequestLongDto> dto = Optional.of(requestService.getById(requestId, userId));

        assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrPropertyWithValue("description", "text");
                    assertThat(i).hasFieldOrProperty("created");
                    assertThat(i.getCreated()).isNotNull();
                    assertThat(i).hasFieldOrProperty("items");
                    assertThat(i.getItems()).hasSize(5);
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("itemId", 1L);
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("itemName", "item1");
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("description", "description1");
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("available", true);
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("requestId", 1L);
                });
    }

    @Test
    @Order(2)
    void getForOwner() {
        long userId = 1L;

        List<ItemRequestLongDto> dtos = requestService.getForOwner(userId);
        Optional<ItemRequestLongDto> dto = Optional.of(dtos.get(0));

        assertThat(dtos)
                .hasSize(1);
        assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrPropertyWithValue("description", "text");
                    assertThat(i).hasFieldOrProperty("created");
                    assertThat(i.getCreated()).isNotNull();
                    assertThat(i).hasFieldOrProperty("items");
                    assertThat(i.getItems()).hasSize(5);
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("itemId", 1L);
                });
    }

    @Test
    @Order(3)
    void getAll() {
        long userId = 2L;
        int from = 0;
        int size = 10;

        List<ItemRequestLongDto> dtos = requestService.getAll(from, size, userId);
        Optional<ItemRequestLongDto> dto = Optional.of(dtos.get(0));

        assertThat(dtos)
                .hasSize(1);
        assertThat(dto)
                .isPresent()
                .hasValueSatisfying(i -> {
                    assertThat(i).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(i).hasFieldOrPropertyWithValue("description", "text");
                    assertThat(i).hasFieldOrProperty("created");
                    assertThat(i.getCreated()).isNotNull();
                    assertThat(i).hasFieldOrProperty("items");
                    assertThat(i.getItems()).hasSize(5);
                    assertThat(i.getItems().get(0)).hasFieldOrPropertyWithValue("itemId", 1L);
                });
    }
}
