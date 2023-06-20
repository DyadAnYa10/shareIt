package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository {

    private volatile Long id = 1L;

    private final Map<Long, User> usersData = new HashMap<>();


    public synchronized User save(User user) {
        user.setId(id++);
        usersData.put(user.getId(), user);

        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersData.get(id));
    }

    public User updateById(User newUser, Long userId) {
        usersData.put(userId, newUser);
        return usersData.get(userId);
    }

    public boolean isExistEmail(String email) {
        long resultCount = usersData.values()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .count();

        return resultCount > 0;
    }

    public List<User> findAll() {
        return new ArrayList<>(usersData.values());
    }

    public void deleteById(Long id) {
        usersData.remove(id);
    }
}
