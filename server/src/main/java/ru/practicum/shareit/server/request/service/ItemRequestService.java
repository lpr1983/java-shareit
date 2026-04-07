package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.dto.ItemRequestDTO;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDTO create(CreateItemRequestDTO dto, int userId);

    List<ItemRequestDTO> getMyRequests(int userId);

    ItemRequestDTO getById(int id);

    List<ItemRequestDTO> getAllRequestsExceptMy(int userId, int from, int size);

    ItemRequest checkExistsAndReturnIt(int itemId);
}
