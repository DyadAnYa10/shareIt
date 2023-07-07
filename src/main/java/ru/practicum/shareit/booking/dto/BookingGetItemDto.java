package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingGetItemDto {
    private long id;
    private long bookerId;
    private LocalDateTime start;

    public BookingGetItemDto(long id, long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }

    private LocalDateTime end;
}
