package ru.practicum.shareit.server.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.model.BookingStatus;
import ru.practicum.shareit.server.booking.model.BookingDatesOfItem;
import ru.practicum.shareit.server.booking.storage.BookingStorage;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.server.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.server.item.dto.ResponseItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.server.item.mappers.CommentMapper;
import ru.practicum.shareit.server.item.mappers.ItemMapper;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.item.storage.CommentStorage;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestService itemRequestService;

    @Override
    public ResponseItemDTO create(CreateItemDTO itemToCreate, int ownerId) {
        log.info("Create item request: ownerId={}, dto={}", ownerId, itemToCreate);

        checkUserExists(ownerId);

        Integer itemRequestId = itemToCreate.getRequestId();
        ItemRequest itemRequest = null;
        if (itemRequestId != null) {
            itemRequest = itemRequestService.checkExistsAndReturnIt(itemRequestId);
        }

        Item item = itemMapper.toEntity(itemToCreate, itemRequest);
        item.setOwnerId(ownerId);

        Item createdItem = itemStorage.save(item);

        log.info("Item created: id={}", createdItem.getId());

        return itemMapper.toResponseDto(createdItem);
    }

    @Override
    public ResponseItemDTO getById(int itemId, int ownerId) {
        Item item = checkItemExistsAndReturnIt(itemId);

        ResponseItemDTO dto = itemMapper.toResponseDto(item);
        if (item.getOwnerId() == ownerId) {
            attachBookingDates(dto);
        }

        attachComments(dto);

        return dto;
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

        ResponseItemDTO dto = itemMapper.toResponseDto(updatedItem);
        attachBookingDates(dto);
        attachComments(dto);

        return dto;
    }

    @Override
    public List<ResponseItemDTO> getItemsOfUser(int ownerId) {
        log.info("Items of user request: ownerId={}", ownerId);

        checkUserExists(ownerId);

        List<Item> items = itemStorage.findByOwnerIdOrderByIdAsc(ownerId);

        List<ResponseItemDTO> dtoList = items.stream().map(itemMapper::toResponseDto).toList();
        attachBookingDates(dtoList);
        attachComments(dtoList);

        return dtoList;
    }

    @Override
    public List<ResponseItemDTO> search(String text) {
        log.info("Search request: text={}", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemStorage.search("%" + text.toLowerCase() + "%");

        return items.stream().map(itemMapper::toResponseDto).toList();
    }

    @Override
    public ResponseCommentDTO createComment(CreateCommentDTO dto, int userId, int itemId) {
        log.info("createComment, dto {}, userId {}, itemId {}", dto, userId, itemId);

        User author = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));

        Item item = checkItemExistsAndReturnIt(itemId);

        LocalDateTime now = LocalDateTime.now();

        boolean hasCompletedBooking = bookingStorage
                .existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                        userId, itemId, BookingStatus.APPROVED, now);

        if (!hasCompletedBooking) {
            throw new ValidationException(
                    String.format("У пользователя с id: %s нет завершенных аренд вещи с id: %s", userId, itemId)
            );
        }

        Comment comment = commentMapper.toEntity(dto, author, item, now);
        Comment savedComment = commentStorage.save(comment);

        return commentMapper.toResponseDto(savedComment);
    }

    private void attachComments(List<ResponseItemDTO> dtoList) {
        if (dtoList.isEmpty()) {
            return;
        }

        List<Integer> ids = dtoList.stream().map(ResponseItemDTO::getId).toList();
        List<Comment> allComments = commentStorage.findAllWithAuthorByItemIds(ids);

        Map<Integer, List<Comment>> mapOfComments = new HashMap<>();

        for (Comment c : allComments) {
            Integer itemId = c.getItem().getId();
            mapOfComments.computeIfAbsent(itemId, k -> new ArrayList<>()).add(c);
        }

        for (ResponseItemDTO dto : dtoList) {
            List<Comment> comments = mapOfComments.get(dto.getId());
            if (comments == null) {
                continue;
            }
            List<ResponseCommentDTO> commentsDTO = comments.stream().map(commentMapper::toResponseDto).toList();
            dto.setComments(commentsDTO);
        }
    }

    private void attachComments(ResponseItemDTO dto) {
        attachComments(List.of(dto));
    }

    private void attachBookingDates(List<ResponseItemDTO> dtoList) {
        if (dtoList.isEmpty()) {
            return;
        }

        List<Integer> ids = dtoList.stream().map(ResponseItemDTO::getId).toList();

        LocalDateTime now = LocalDateTime.now();
        List<BookingDatesOfItem> listOfDates = bookingStorage.getLastAndNextBookingDatesOfItems(ids,
                now,
                BookingStatus.APPROVED);

        Map<Integer, BookingDatesOfItem> mapOfDates = listOfDates.stream()
                .collect(Collectors.toMap(BookingDatesOfItem::getItemId, Function.identity()));

        for (ResponseItemDTO dto : dtoList) {
            BookingDatesOfItem dates = mapOfDates.get(dto.getId());
            if (dates == null) {
                continue;
            }

            dto.setLastBooking(dates.getLastStart());
            dto.setNextBooking(dates.getNextStart());
        }
    }

    private void attachBookingDates(ResponseItemDTO dto) {
        attachBookingDates(List.of(dto));
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
