package ru.practicum.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidateCommentException extends RuntimeException {
    public ValidateCommentException(String message) {
        super(message);
    }
}
