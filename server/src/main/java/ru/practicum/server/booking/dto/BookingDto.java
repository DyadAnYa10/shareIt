package ru.practicum.server.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
