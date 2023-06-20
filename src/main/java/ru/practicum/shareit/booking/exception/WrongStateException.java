package ru.practicum.shareit.booking.exception;

public class WrongStateException extends RuntimeException {
    public WrongStateException(String state) {
        super(state);
    }
}
