package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getUserById(Long userId);

    void deleteUserById(Long id);
}
