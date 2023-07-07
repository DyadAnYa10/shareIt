package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class ItemRequestLongDto {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private final List<ShortItemResponseDto> items;

    @Builder(toBuilder = true)
    @Getter
    public static class ShortItemResponseDto {
        @JsonProperty(value = "id")
        private final long itemId;
        @JsonProperty(value = "name")
        private final String itemName;
        @JsonProperty(value = "description")
        private final String description;
        @JsonProperty(value = "available")
        private final Boolean available;
        @JsonProperty(value = "requestId")
        private final Long requestId;

    }
}
