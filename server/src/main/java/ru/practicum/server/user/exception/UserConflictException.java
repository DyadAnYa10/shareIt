package ru.practicum.server.user.exception;

public class UserConflictException extends Throwable {
    public UserConflictException(String s) {
        super(s);
    }
}
