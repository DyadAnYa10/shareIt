package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotNull(groups = {Create.class, Update.class})
    private Long itemId;
    @NotNull(groups = {Update.class})
    @Future
    private LocalDateTime start;
    @Future
    @NotNull(groups = { Update.class})
    private LocalDateTime end;
}
