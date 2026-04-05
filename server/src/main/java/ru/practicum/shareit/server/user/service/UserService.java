package ru.practicum.shareit.server.user.service;

import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.server.user.dto.ResponseUserDTO;

import java.util.List;

public interface UserService {

    List<ResponseUserDTO> getAll();

    ResponseUserDTO getById(int id);

    ResponseUserDTO create(CreateUserDTO createUserDTO);

    ResponseUserDTO update(int id, UpdateUserDTO updateUserDTO);

    void delete(int id);
}
