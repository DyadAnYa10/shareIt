package ru.practicum.server.booking.exception;

public class WrongStateException extends RuntimeException {
    public WrongStateException(String state) {
        super(state);
    }
}
