package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDTO;
import ru.practicum.shareit.user.dto.UpdateUserDTO;
import ru.practicum.shareit.user.dto.ResponseUserDTO;

public interface UserService {
    ResponseUserDTO getById(int id);

    ResponseUserDTO create(CreateUserDTO createUserDTO);

    ResponseUserDTO update(int id, UpdateUserDTO updateUserDTO);

    void delete(int id);
}
