package ru.practicum.shareit.item;//package ru.practicum.shareit.item;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.ItemIncomeDto;
//import ru.practicum.shareit.item.impl.ItemMapper;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.model.Comment;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//class ItemMapperTest {
//
//    @Test
//    void toItemTest() {
//        ItemDto incomeDto = ItemDto.builder()
//                .name("name")
//                .description("description")
//                .available(true)
//                .requestId(1L)
//                .build();
//
//        Item item = ItemMapper.toItem(incomeDto, new User());
//
//        Assertions.assertThat(item)
//                .hasFieldOrPropertyWithValue("name", "name")
//                .hasFieldOrPropertyWithValue("description", "description")
//                .hasFieldOrPropertyWithValue("available", true);
//    }
//
//    @Test
//    void toItemDto() {
//        Item item = fillEntity();
//
//        ItemDto itemDto = ItemMapper.toItemDto(item, List.of());
//
//        Assertions.assertThat(itemDto)
//                .hasFieldOrPropertyWithValue("id", 1L)
//                .hasFieldOrPropertyWithValue("name", "itemName")
//                .hasFieldOrPropertyWithValue("description", "itemDescription")
//                .hasFieldOrPropertyWithValue("available", true)
//                .hasFieldOrProperty("owner")
//                .hasFieldOrProperty("comments")
//                .hasFieldOrPropertyWithValue("requestId", null);
//    }
//
//    @Test
//    void toItemDtosTest() {
//        List<Item> items = List.of(fillEntity());
//        Map<Item, List<Comment>> comments = Map.of(items.get(0), List.of());
//
//        List<ItemDto> itemDtos = ItemMapper.toItemDto(items, comments);
//
//        Assertions.assertThat(itemDtos)
//                .hasSize(1);
//    }
//
//    @Test
//    void itemDtoForOwnerTest() {
//        Item item = fillEntity();
//        List<Booking> bookings = List.of();
//        List<Comment> comments = List.of();
//
//        Optional<ItemDto> itemDto = Optional.of(ItemMapper.itemDtoForOwner(item, bookings, comments));
//
//        Assertions.assertThat(itemDto)
//                .isPresent()
//                .hasValueSatisfying(i -> {
//                    Assertions.assertThat(i)
//                            .hasFieldOrPropertyWithValue("id", 1L)
//                            .hasFieldOrPropertyWithValue("name", "itemName")
//                            .hasFieldOrPropertyWithValue("description", "itemDescription")
//                            .hasFieldOrPropertyWithValue("available", true)
//                            .hasFieldOrProperty("owner")
//                            .hasFieldOrProperty("lastBooking")
//                            .hasFieldOrProperty("nextBooking")
//                            .hasFieldOrProperty("comments")
//                            .hasFieldOrPropertyWithValue("requestId", null);
//                    Assertions.assertThat(i.getLastBooking())
//                            .isNull();
//                    Assertions.assertThat(i.getNextBooking())
//                            .isNull();
//                        });
//    }
//
//    private Item fillEntity() {
//        User user = new User();
//        user.setId(1L);
//        user.setName("name");
//        user.setEmail("user@mail.com");
//
//        Item item = new Item();
//        item.setId(1L);
//        item.setName("itemName");
//        item.setDescription("itemDescription");
//        item.setAvailable(true);
//        item.setOwner(user);
//
//        return item;
//    }
//}
