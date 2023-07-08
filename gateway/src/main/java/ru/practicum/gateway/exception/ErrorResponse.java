package ru.practicum.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String error;
    private String description;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
