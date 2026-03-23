package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserInMemoryStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int counter;

    @Override
    public List<User> getAll() {
        return users.values().stream()
                .sorted(Comparator.comparingInt(User::getId))
                .toList();
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean userExists(int id) {
        return users.containsKey(id);
    }

    @Override
    public int create(User user) {
        int newId = getNewId();
        user.setId(newId);
        users.put(newId, user);

        return newId;
    }

    @Override
    public boolean emailExists(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void update(User user) {
        int id = user.getId();
        users.put(id, user);
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    private int getNewId() {
        counter++;
        return counter;
    }
}
