package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingGetItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    @NotBlank(message = "Description cannot be empty or null")
    private String description;

    @NotNull(message = "Available cannot be null")
    private Boolean available;

    private User owner;


    private BookingGetItemDto lastBooking;
    private BookingGetItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
