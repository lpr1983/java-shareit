package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.server.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.server.item.dto.ResponseItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;

import java.util.List;

public interface ItemService {
    ResponseItemDTO create(CreateItemDTO itemToCreate, int ownerId);

    ResponseItemDTO update(UpdateItemDTO itemToUpdate, int itemId, int ownerId);

    ResponseItemDTO getById(int itemId, int ownerId);

    List<ResponseItemDTO> getItemsOfUser(int ownerId);

    List<ResponseItemDTO> search(String text);

    ResponseCommentDTO createComment(CreateCommentDTO dto, int userId, int itemId);
}
