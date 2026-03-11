package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.AlgorithmFailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDTO;
import ru.practicum.shareit.item.dto.ResponseItemDTO;
import ru.practicum.shareit.item.dto.UpdateItemDTO;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ResponseItemDTO create(CreateItemDTO itemToCreate, int ownerId) {
        log.info("Create item request: ownerId={}, dto={}", ownerId, itemToCreate);

        checkUserExists(ownerId);

        Item item = itemMapper.toEntity(itemToCreate);
        item.setOwnerId(ownerId);
        int id = itemStorage.create(item);

        Item createdItem = itemStorage.getById(id)
                .orElseThrow(() -> new AlgorithmFailException("Не найден созданный элемент, id: " + id));

        log.info("Item created: id={}", id);

        return itemMapper.toResponseDto(createdItem);
    }

    @Override
    public ResponseItemDTO getById(int itemId) {
        Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        return itemMapper.toResponseDto(item);
    }

    @Override
    public ResponseItemDTO update(UpdateItemDTO updateItemDTO, int itemId, int ownerId) {
        log.info("Update item request: ownerId={}, itemId={}, dto={}", ownerId, itemId, updateItemDTO);

        checkUserExists(ownerId);

        Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        if (item.getOwnerId() != ownerId) {
            throw new NotFoundException(String.format("У пользователя %s нет вещи %s",
                    ownerId, itemId));
        }

        itemMapper.update(item, updateItemDTO);

        itemStorage.update(item);

        Item updatedItem = itemStorage.getById(itemId)
                .orElseThrow(() -> new AlgorithmFailException("Не найден обновленный элемент, id: " + itemId));

        return itemMapper.toResponseDto(updatedItem);
    }

    @Override
    public List<ResponseItemDTO> getItemsOfUser(int ownerId) {
        log.info("Items of user request: ownerId={}", ownerId);

        checkUserExists(ownerId);

        return itemStorage.getItemsOfUser(ownerId)
                .stream()
                .map(itemMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ResponseItemDTO> search(String text) {
        log.info("Search request: text={}", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.search(text)
                .stream()
                .map(itemMapper::toResponseDto)
                .toList();
    }

    private void checkUserExists(int id) {
        if (!userStorage.userExists(id)) {
            throw new NotFoundException("Не найден пользователь с id: " + id);
        }
    }
}
