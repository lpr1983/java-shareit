package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int counter;

    @Override
    public int create(Item item) {
        int newId = getId();
        item.setId(newId);
        items.put(newId, item);

        return newId;
    }

    @Override
    public List<Item> getItemsOfUser(int ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwnerId() == ownerId)
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        String textLowerCase = text.toLowerCase();

        return items.values().stream()
                .filter(i ->
                        i.getAvailable()
                                && (i.getName().toLowerCase().contains(textLowerCase)
                                || i.getDescription().toLowerCase().contains(textLowerCase)))
                .toList();
    }

    @Override
    public void update(Item item) {
        int itemId = item.getId();
        items.put(itemId, item);
    }

    @Override
    public Optional<Item> getById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public boolean itemExists(int id) {
        return items.containsKey(id);
    }

    private int getId() {
        counter++;
        return counter;
    }
}
