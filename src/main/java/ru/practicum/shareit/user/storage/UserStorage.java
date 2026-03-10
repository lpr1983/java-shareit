package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {

    Optional<User> getById(int id);

    int create(User user);

    void update(User user);

    void delete(int id);

    boolean emailExists(String email);

    boolean userExists(int id);
}
