package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

        Item createdItem = itemStorage.save(item);

        log.info("Item created: id={}", createdItem.getId());

        return itemMapper.toResponseDto(createdItem);
    }

    @Override
    public ResponseItemDTO getById(int itemId) {
        Item item = checkItemExistsAndReturnIt(itemId);

        return itemMapper.toResponseDto(item);
    }

    @Override
    public ResponseItemDTO update(UpdateItemDTO updateItemDTO, int itemId, int ownerId) {
        log.info("Update item request: ownerId={}, itemId={}, dto={}", ownerId, itemId, updateItemDTO);

        checkUserExists(ownerId);

        Item item = checkItemExistsAndReturnIt(itemId);

        if (item.getOwnerId() != ownerId) {
            throw new NotFoundException(String.format("У пользователя %s нет вещи %s",
                    ownerId, itemId));
        }

        itemMapper.update(item, updateItemDTO);

        Item updatedItem = itemStorage.save(item);

        return itemMapper.toResponseDto(updatedItem);
    }

    @Override
    public List<ResponseItemDTO> getItemsOfUser(int ownerId) {
        log.info("Items of user request: ownerId={}", ownerId);

        checkUserExists(ownerId);

        return itemStorage.findByOwnerId(ownerId)
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

        return itemStorage.search("%" + text.toLowerCase() + "%")
                .stream()
                .map(itemMapper::toResponseDto)
                .toList();
    }

    private Item checkItemExistsAndReturnIt(int itemId) {

        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));
    }

    private void checkUserExists(int id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Не найден пользователь с id: " + id);
        }
    }
}
