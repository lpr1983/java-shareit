package ru.practicum.shareit.server.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.server.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerIdOrderByIdAsc(int ownerId);

    @Query("""
            select it from Item as it
            where it.available
            and (lower(it.name) like ?1 or lower(it.description) like ?1)
            order by it.id
            """)
    List<Item> search(String pattern);
}
