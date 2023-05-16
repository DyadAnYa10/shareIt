package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(Long userId);

    User findUserById(Long userId);

    void deleteUserById(Long id);
}
