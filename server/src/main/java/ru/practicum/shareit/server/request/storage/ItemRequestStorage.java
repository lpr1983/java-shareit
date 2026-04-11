package ru.practicum.shareit.server.request.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByUser_IdOrderByCreatedDesc(int userId);

    List<ItemRequest> findAllByUser_IdNotOrderByCreatedDesc(Integer userId, PageRequest page);
}
