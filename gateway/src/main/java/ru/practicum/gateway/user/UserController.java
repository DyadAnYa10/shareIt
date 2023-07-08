package ru.practicum.gateway.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDTO user) {
        log.info("Получен запрос к эндпоинту /users create");
        return userClient.createUser(user);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Получен запрос к эндпоинту: /users getAll");
        return userClient.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable("id") Long userId) {
        log.info("Получен запрос к эндпоинту: /users geById с id={}", userId);
        return userClient.findUserById(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long userId,
                                             @RequestBody UserDTO userDto) {
        log.info("Получен запрос к эндпоинту: /users update с id={}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteUserById(@PathVariable("id") @Positive Long userId) {
        log.info("Получен запрос к эндпоинту: /users delete с id={}", userId);
        userClient.deleteUserById(userId);
        return HttpStatus.OK;
    }

}
