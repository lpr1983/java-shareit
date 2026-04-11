package ru.practicum.shareit.server.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.server.request.storage.ItemRequestStorage;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestStorage itemRequestStorage;

    @Mock
    UserService userService;

    @Mock
    ItemStorage itemStorage;

    @Mock
    ItemRequestMapper mapper;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void create_userNotFound_thenThrowNotFound() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("Нужна дрель")
                .build();

        Mockito.when(userService.checkUserExistsAndReturnIt(1))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 1"));

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.create(dto, 1));

        Mockito.verify(userService).checkUserExistsAndReturnIt(1);
        Mockito.verifyNoInteractions(itemRequestStorage, itemStorage, mapper);
    }

    @Test
    void getMyRequests_userNotFound_thenThrowNotFound() {
        Mockito.when(userService.checkUserExistsAndReturnIt(1))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 1"));

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getMyRequests(1));

        Mockito.verify(userService).checkUserExistsAndReturnIt(1);
        Mockito.verifyNoInteractions(itemRequestStorage, itemStorage, mapper);
    }

    @Test
    void getById_requestNotFound_thenThrowNotFound() {
        Mockito.when(itemRequestStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getById(10));

        Mockito.verify(itemRequestStorage).findById(10);
        Mockito.verifyNoInteractions(userService, itemStorage, mapper);
    }

    @Test
    void getAllRequestsExceptMy_userNotFound_thenThrowNotFound() {
        Mockito.when(userService.checkUserExistsAndReturnIt(1))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 1"));

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestsExceptMy(1, 0, 20));

        Mockito.verify(userService).checkUserExistsAndReturnIt(1);
        Mockito.verifyNoInteractions(itemRequestStorage, itemStorage, mapper);
    }

    @Test
    void checkExistsAndReturnIt_notFound_thenThrowNotFound() {
        Mockito.when(itemRequestStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.checkExistsAndReturnIt(10));

        Mockito.verify(itemRequestStorage).findById(10);
        Mockito.verifyNoInteractions(userService, itemStorage, mapper);
    }

}