package ru.practicum.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownBookingException extends RuntimeException {
    public UnknownBookingException(String message) {
        super(message);
    }
}
