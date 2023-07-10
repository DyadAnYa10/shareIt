package ru.practicum.server.item.dto;

import lombok.*;
import ru.practicum.server.booking.dto.BookingGetItemDto;
import ru.practicum.server.user.model.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingGetItemDto lastBooking;
    private BookingGetItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
