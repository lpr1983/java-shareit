package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.request.dto.ItemRequestDTO;
import ru.practicum.shareit.server.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.storage.ItemRequestStorage;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDTO create(CreateItemRequestDTO dto, int userId) {
        log.debug("Создание запроса вещи: userId={}, description={}", userId, dto.getDescription());

        User user = userService.checkUserExistsAndReturnIt(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);

        itemRequest = itemRequestStorage.save(itemRequest);

        log.debug("Запрос вещи создан: requestId={}, userId={}", itemRequest.getId(), userId);

        return mapper.toResponseDto(itemRequest, null);
    }

    @Override
    public List<ItemRequestDTO> getMyRequests(int userId) {
        log.debug("Получение своих запросов вещей: userId={}", userId);

        userService.checkUserExistsAndReturnIt(userId);

        List<ItemRequest> itemRequests = itemRequestStorage.findAllByUser_IdOrderByCreatedDesc(userId);

        log.debug("Найдено запросов вещей пользователя: userId={}, count={}", userId, itemRequests.size());

        return getDTOListWithItems(itemRequests);
    }

    @Override
    public ItemRequestDTO getById(int id) {
        log.debug("Получение запроса вещи по id: requestId={}", id);

        ItemRequest itemRequest = checkExistsAndReturnIt(id);

        List<Item> items = itemStorage.findAllByItemRequest_IdOrderByIdAsc(id);

        log.debug("Для запроса вещи requestId={} найдено items: count={}", id, items.size());

        return mapper.toResponseDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDTO> getAllRequestsExceptMy(int userId, int from, int size) {
        log.debug("Получение чужих запросов вещей: userId={}, from={}, size={}", userId, from, size);

        userService.checkUserExistsAndReturnIt(userId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByUser_IdNotOrderByCreatedDesc(userId, page);

        log.debug("Найдено чужих запросов вещей: userId={}, count={}", userId, itemRequests.size());

        return itemRequests.stream()
                .map(r -> mapper.toResponseDto(r, null))
                .toList();
    }

    @Override
    public ItemRequest checkExistsAndReturnIt(int itemId) {
        log.debug("Проверка существования запроса вещи: requestId={}", itemId);

        return itemRequestStorage.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Запрос вещи не найден: requestId={}", itemId);
                    return new NotFoundException("Не найден запрос вещи с id: " + itemId);
                });
    }

    private List<ItemRequestDTO> getDTOListWithItems(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            log.debug("Список запросов вещей пуст");
            return Collections.emptyList();
        }

        List<Integer> ids = itemRequests.stream().map(ItemRequest::getId).toList();
        log.debug("Загрузка items для запросов вещей: requestIds={}", ids);

        List<Item> itemsByIds = itemStorage.findAllByItemRequest_IdInOrderByIdAsc(ids);

        log.debug("Загружено items для запросов вещей: count={}", itemsByIds.size());

        Map<Integer, List<Item>> mapOfItems = new HashMap<>();
        for (Item i : itemsByIds) {
            Integer itemRequestId = i.getItemRequest().getId();
            if (itemRequestId == null) {
                log.warn("У вещи itemId={} отсутствует itemRequest.id при группировке", i.getId());
                continue;
            }
            mapOfItems.computeIfAbsent(itemRequestId, k -> new ArrayList<>())
                    .add(i);
        }

        log.debug("Сгруппировано items по запросам вещей: requestCountWithItems={}", mapOfItems.size());

        return itemRequests.stream()
                .map(r -> mapper.toResponseDto(r,
                        mapOfItems.getOrDefault(r.getId(), Collections.emptyList())))
                .toList();
    }
}