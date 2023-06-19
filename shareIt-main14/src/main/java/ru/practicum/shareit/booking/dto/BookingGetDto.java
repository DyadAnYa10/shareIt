package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.model.User;

import javax.persistence.Enumerated;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingGetDto {
    @NotNull(groups = {Create.class, Update.class})
    private long id;
    @NotNull(groups = {Create.class, Update.class})
    @Future
    private LocalDateTime start;
    @Future
    @NotNull(groups = {Create.class, Update.class})
    private LocalDateTime end;
    private Item item;
    private User booker;
    @Enumerated
    private BookingStatus status;
}
