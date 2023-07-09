package ru.practicum.server.request.dto;

import lombok.*;
import ru.practicum.server.item.dto.ItemShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private List<ItemShortDto> items;
    private LocalDateTime created;
    private Long requestorId;
}
