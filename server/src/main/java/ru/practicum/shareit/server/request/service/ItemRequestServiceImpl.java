package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.storage.ItemRequestStorage;
import ru.practicum.shareit.server.request.dto.ItemRequestDTO;
import ru.practicum.shareit.server.request.mappers.ItemRequestMapper;
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
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserService userService;
    private final ItemStorage itemStorage;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDTO create(CreateItemRequestDTO dto, int userId) {

        User user = userService.checkUserExistsAndReturnIt(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);

        itemRequest = itemRequestStorage.save(itemRequest);

        return mapper.toResponseDto(itemRequest, null);
    }

    @Override
    public List<ItemRequestDTO> getMyRequests(int userId) {
        userService.checkUserExistsAndReturnIt(userId);

        List<ItemRequest> itemRequests = itemRequestStorage.findAllByUser_IdOrderByCreatedDesc(userId);

        return getDTOListWithItems(itemRequests);
    }

    @Override
    public ItemRequestDTO getById(int id) {
        ItemRequest itemRequest = checkExistsAndReturnIt(id);

        List<Item> items = itemStorage.findAllByItemRequest_IdOrderByIdAsc(id);

        return mapper.toResponseDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDTO> getAllRequestsExceptMy(int userId, int from, int size) {
        userService.checkUserExistsAndReturnIt(userId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = itemRequestStorage.findAllByUser_IdNotOrderByCreatedDesc(userId, page);

        return itemRequests.stream().map(r -> mapper.toResponseDto(r, null)).toList();
    }

    @Override
    public ItemRequest checkExistsAndReturnIt(int itemId) {
        return itemRequestStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос вещи с id: " + itemId));
    }

    private List<ItemRequestDTO> getDTOListWithItems(List<ItemRequest> itemRequests) {
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> ids = itemRequests.stream().map(ItemRequest::getId).toList();
        List<Item> itemsByIds = itemStorage.findAllByItemRequest_IdInOrderByIdAsc(ids);

        Map<Integer, List<Item>> mapOfItems = new HashMap<>();
        for (Item i : itemsByIds) {
            Integer itemRequestId = i.getItemRequest().getId();
            if (itemRequestId == null) {
                continue;
            }
            mapOfItems.computeIfAbsent(itemRequestId, k -> new ArrayList<>())
                    .add(i);
        }

        return itemRequests.stream().
                map(r -> mapper.toResponseDto(r,
                        mapOfItems.getOrDefault(r.getId(), Collections.emptyList())))
                .toList();
    }
}
