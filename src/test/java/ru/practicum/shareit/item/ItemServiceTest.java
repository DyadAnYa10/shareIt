package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingCreateException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.exception.ItemExistsException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserConflictException;
import ru.practicum.shareit.user.exception.UserExistsException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {
        "spring.config.name=application-test",
        "spring.config.location=classpath:application-test.properties"
}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {

    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    private UserDto ownerDto = UserDto.builder()
            .name("name")
            .email("user@email.com")
            .build();

    private UserDto owner2Dto = UserDto.builder()
            .name("name2")
            .email("user2@email.com")
            .build();
    private UserDto bookerDto = UserDto.builder()
            .name("Test booker")
            .email("booker@email.ru")
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();
    private ItemDto item2Dto = ItemDto.builder()
            .name("name2")
            .description("description 2")
            .available(true)
            .build();

    @Test
    void createItemTest() {
        UserDto owner = userService.create(ownerDto);
        User inputUser = UserMapper.toUser(owner);

        ItemDto item = itemService.create(itemDto, owner.getId());
        Item inputItem = ItemMapper.toItem(item, inputUser);

        assertNotNull(inputItem);
        assertNotNull(inputItem.getId());
        assertEquals(inputUser.getId(), inputItem.getOwner().getId());
    }

    @Test
    void findAllItemsByUserIdTest() {
        UserDto owner = userService.create(ownerDto);

        itemService.create(itemDto, owner.getId());
        itemService.create(item2Dto, owner.getId());

        Long userId = owner.getId();

        List<ItemDto> itemResponseDtoList = itemService.getAllByUserId(0, 10, userId);

        assertNotNull(itemResponseDtoList);
        assertEquals(2, itemResponseDtoList.size());
    }

    @Test
    void findItemByIdTest() {
        UserDto owner = userService.create(ownerDto);

        ItemDto item = itemService.create(itemDto, owner.getId());

        Long itemId = item.getId();
        Long userId = owner.getId();

        ItemDto itemResponseDto = itemService.getItemById(itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    void updateItemTest() {
        UserDto owner = userService.create(ownerDto);

        ItemDto item = itemService.create(itemDto, owner.getId());

        Long itemId = item.getId();
        Long userId = owner.getId();

        ItemDto itemResponseDto = itemService.updateById(item, itemId, userId);

        assertNotNull(itemResponseDto);
    }

    @Test
    void findItemsByRequestTest() {
        UserDto owner = userService.create(ownerDto);

        itemService.create(itemDto, owner.getId());
        itemService.create(item2Dto, owner.getId());

        Item item3 = new Item();
        item3.setName("Another Item");
        item3.setDescription("test item");
        item3.setAvailable(false);
        ItemDto inputItem = ItemMapper.toItemDto(item3);
        itemService.create(inputItem, owner.getId());

        List<ItemDto> result = itemService.searchByText("Description", 0, 10);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getDescription().equals("description")));
        assertTrue(result.stream().anyMatch(item -> item.getDescription().equals("description 2")));
    }

    @Test
    void addCommentTest() throws UserExistsException, ItemExistsException, UserConflictException, BookingCreateException, CommentException {
        UserDto owner = userService.create(ownerDto);
        UserDto booker = userService.create(bookerDto);

        ItemDto item = itemService.create(itemDto, owner.getId());
        item.setOwner(UserMapper.toUser(owner));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        bookingService.createBooking(bookingDto, booker.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        CommentDto commentResponseDto = itemService.createComment(commentDto, item.getId(), booker.getId());

        assertNotNull(commentResponseDto.getId());
        assertEquals(commentDto.getText(), commentResponseDto.getText());
        assertEquals(booker.getName(), commentResponseDto.getAuthorName());
    }

}