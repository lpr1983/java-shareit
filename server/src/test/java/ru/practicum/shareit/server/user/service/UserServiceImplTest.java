package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.server.error.exception.ConflictException;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserStorage userStorage;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void getById_checkUserNotExists_thenThrowNotFound() {
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getById(1));
    }

    @Test
    void create_checkEmailExists_thenThrowConflict() {
        CreateUserDTO dto = CreateUserDTO.builder().name("name").email("email").build();
        Mockito.when(userStorage.existsByEmail("email")).thenReturn(true);

        Assertions.assertThrows(ConflictException.class, () -> userService.create(dto));
    }

    @Test
    void update_checkUserNotExists_thenThrowNotFound() {
        UpdateUserDTO dto = UpdateUserDTO.builder().name("").build();
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.update(1, dto));
    }

    @Test
    void update_checkConflictEmai_thenThrowConflict() {
        UpdateUserDTO dto = UpdateUserDTO.builder().email("").build();
        Mockito.when(userStorage.existsByEmail("")).thenReturn(true);
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(User.builder().id(1).build()));

        Assertions.assertThrows(ConflictException.class, () -> userService.update(1, dto));
    }

    @Test
    void delete_checkUserNotExists_thenThrowNotFound() {
        Mockito.when(userStorage.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.delete(1));
    }

}