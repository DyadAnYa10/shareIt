package ru.practicum.server.user.exception;

import lombok.Generated;

@Generated
public class ExistEmailException extends RuntimeException {
    public ExistEmailException(String message) {
        super(message);
    }
}
