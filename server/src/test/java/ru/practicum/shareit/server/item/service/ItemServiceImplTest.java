package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.storage.BookingStorage;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.item.mappers.CommentMapper;
import ru.practicum.shareit.server.item.mappers.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.CommentStorage;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemMapper itemMapper;

    @Mock
    CommentMapper commentMapper;

    @Mock
    ItemStorage itemStorage;

    @Mock
    UserStorage userStorage;

    @Mock
    BookingStorage bookingStorage;

    @Mock
    CommentStorage commentStorage;

    @Mock
    ItemRequestService itemRequestService;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void create_ownerNotFound_thenThrowNotFound() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("дрель")
                .description("мощная дрель")
                .available(true)
                .build();

        Mockito.when(userStorage.existsById(1)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemService.create(dto, 1));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verifyNoMoreInteractions(userStorage);
        Mockito.verifyNoInteractions(itemStorage, itemMapper, itemRequestService, bookingStorage, commentStorage, commentMapper);
    }

    @Test
    void create_requestNotFound_thenThrowNotFound() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("дрель")
                .description("мощная дрель")
                .available(true)
                .requestId(7)
                .build();

        Mockito.when(userStorage.existsById(1)).thenReturn(true);
        Mockito.when(itemRequestService.checkExistsAndReturnIt(7))
                .thenThrow(new NotFoundException("Не найден запрос с id: 7"));

        Assertions.assertThrows(NotFoundException.class, () -> itemService.create(dto, 1));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verify(itemRequestService).checkExistsAndReturnIt(7);
        Mockito.verifyNoInteractions(itemStorage, itemMapper, bookingStorage, commentStorage, commentMapper);
    }

    @Test
    void getById_itemNotFound_thenThrowNotFound() {
        Mockito.when(itemStorage.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.getById(1, 2));

        Mockito.verify(itemStorage).findById(1);
        Mockito.verifyNoInteractions(itemMapper, bookingStorage, commentStorage, commentMapper, userStorage, itemRequestService);
    }

    @Test
    void update_ownerNotFound_thenThrowNotFound() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("новое имя")
                .build();

        Mockito.when(userStorage.existsById(1)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemService.update(dto, 10, 1));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verifyNoInteractions(itemStorage, itemMapper, bookingStorage, commentStorage, commentMapper, itemRequestService);
    }

    @Test
    void update_itemNotFound_thenThrowNotFound() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("новое имя")
                .build();

        Mockito.when(userStorage.existsById(1)).thenReturn(true);
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.update(dto, 10, 1));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verifyNoInteractions(itemMapper, bookingStorage, commentStorage, commentMapper, itemRequestService);
    }

    @Test
    void update_notOwner_thenThrowNotFound() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("новое имя")
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(2)
                .name("старая вещь")
                .description("старое описание")
                .available(true)
                .build();

        Mockito.when(userStorage.existsById(1)).thenReturn(true);
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotFoundException.class, () -> itemService.update(dto, 10, 1));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verifyNoInteractions(bookingStorage, commentStorage, commentMapper, itemRequestService);
        Mockito.verify(itemMapper, Mockito.never()).update(Mockito.any(), Mockito.any());
        Mockito.verify(itemStorage, Mockito.never()).save(Mockito.any());
    }

    @Test
    void getItemsOfUser_ownerNotFound_thenThrowNotFound() {
        Mockito.when(userStorage.existsById(1)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemsOfUser(1));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verifyNoInteractions(itemStorage, itemMapper, bookingStorage, commentStorage, commentMapper, itemRequestService);
    }

    @Test
    void search_blankText_thenReturnEmptyList() {
        Assertions.assertEquals(Collections.emptyList(), itemService.search("   "));

        Mockito.verifyNoInteractions(itemStorage, itemMapper, bookingStorage, commentStorage, commentMapper, userStorage, itemRequestService);
    }

    @Test
    void createComment_userNotFound_thenThrowNotFound() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("отличная вещь")
                .build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.createComment(dto, 1, 10));

        Mockito.verify(userStorage).findById(1);
        Mockito.verifyNoInteractions(itemStorage, bookingStorage, commentStorage, commentMapper, itemMapper, itemRequestService);
    }

    @Test
    void createComment_itemNotFound_thenThrowNotFound() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("отличная вещь")
                .build();

        User user = User.builder().id(1).name("Иван").email("ivan@test.ru").build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemService.createComment(dto, 1, 10));

        Mockito.verify(userStorage).findById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verifyNoInteractions(bookingStorage, commentStorage, commentMapper, itemMapper, itemRequestService);
    }

    @Test
    void createComment_noCompletedBooking_thenThrowValidation() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("отличная вещь")
                .build();

        User user = User.builder().id(1).name("Иван").email("ivan@test.ru").build();
        Item item = Item.builder()
                .id(10)
                .ownerId(2)
                .name("дрель")
                .description("мощная дрель")
                .available(true)
                .build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.of(item));
        Mockito.when(bookingStorage.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                        Mockito.eq(1),
                        Mockito.eq(10),
                        Mockito.eq(BookingStatus.APPROVED),
                        Mockito.any()))
                .thenReturn(false);

        Assertions.assertThrows(ValidationException.class, () -> itemService.createComment(dto, 1, 10));

        Mockito.verify(userStorage).findById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verify(bookingStorage).existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                Mockito.eq(1),
                Mockito.eq(10),
                Mockito.eq(BookingStatus.APPROVED),
                Mockito.any());
        Mockito.verifyNoInteractions(commentStorage, commentMapper, itemMapper, itemRequestService);
    }
}