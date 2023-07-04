package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemRequest {

    @NotBlank(groups = Create.class)
    private final String description;
    private Long id;
}
