package ru.practicum.server.booking.exception;

public class BookingExistsException extends Throwable {
    public BookingExistsException(String s) {
        super(s);
    }
}
