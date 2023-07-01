package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class ItemRequestDto {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private final ShortRequestorDto requestor;

    @RequiredArgsConstructor
    @Getter
    public static class ShortRequestorDto {
        @JsonProperty(value = "id")
        private final long requestorId;
        @JsonProperty(value = "name")
        private final String requestorName;
    }
}
