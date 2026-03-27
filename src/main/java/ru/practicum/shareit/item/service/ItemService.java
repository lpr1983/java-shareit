package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateCommentDTO;
import ru.practicum.shareit.item.dto.CreateItemDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.dto.ResponseItemDTO;
import ru.practicum.shareit.item.dto.UpdateItemDTO;

import java.util.List;

public interface ItemService {
    ResponseItemDTO create(CreateItemDTO itemToCreate, int ownerId);

    ResponseItemDTO update(UpdateItemDTO itemToUpdate, int itemId, int ownerId);

    ResponseItemDTO getById(int itemId);

    List<ResponseItemDTO> getItemsOfUser(int ownerId);

    List<ResponseItemDTO> search(String text);

    ResponseCommentDTO createComment(CreateCommentDTO dto, int userId, int itemId);
}
