package ru.practicum.server.booking.exception;

public class BookingStateException extends Throwable {
    public BookingStateException(String state) {
        super(state);
    }
}
