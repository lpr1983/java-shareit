package ru.practicum.shareit.server.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.model.User;

public interface UserStorage extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
}
