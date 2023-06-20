package ru.practicum.shareit.booking.exception;

public class BookingStateException extends Throwable {
    public BookingStateException(String state) {
        super(state);
    }
}
