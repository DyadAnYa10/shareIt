package ru.practicum.server.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
