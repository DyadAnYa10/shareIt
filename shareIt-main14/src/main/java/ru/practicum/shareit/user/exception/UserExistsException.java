package ru.practicum.shareit.user.exception;

public class UserExistsException extends Throwable {
    public UserExistsException(String s) {
        super(s);
    }
}
