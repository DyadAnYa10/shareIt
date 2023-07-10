package ru.practicum.server.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;
import ru.practicum.server.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toListUserDtos(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDTO, Long userId) {
        User user = patchUser(userDTO, userId);
        user.setId(userId);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id='%S' not found", userId)));

        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User patchUser(UserDto patch, Long userId) {
        UserDto entry = getUserById(userId);
        String name = patch.getName();
        if (StringUtils.hasText(name)) {
            entry.setName(name);
        }

        String oldEmail = entry.getEmail();
        String newEmail = patch.getEmail();
        if (StringUtils.hasText(newEmail) && !oldEmail.equals(newEmail)) {
            entry.setEmail(newEmail);
        }
        return UserMapper.toUser(entry);
    }
}
