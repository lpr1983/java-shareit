package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    int create(Item item);

    void update(Item item);

    Optional<Item> getById(int id);

    List<Item> getItemsOfUser(int ownerId);

    List<Item> search(String text);

    boolean itemExists(int id);
}
