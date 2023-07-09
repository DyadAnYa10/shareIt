package ru.practicum.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingStateException extends RuntimeException {
    public BookingStateException(String message) {
        super(message);
    }
}
