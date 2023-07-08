package ru.practicum.server.user.exception;

public class UserExistsException extends Throwable {
    public UserExistsException(String s) {
        super(s);
    }
}
