package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDTO;
import ru.practicum.shareit.user.dto.UpdateUserDTO;
import ru.practicum.shareit.user.dto.ResponseUserDTO;

import java.util.List;

public interface UserService {

    List<ResponseUserDTO> getAll();

    ResponseUserDTO getById(int id);

    ResponseUserDTO create(CreateUserDTO createUserDTO);

    ResponseUserDTO update(int id, UpdateUserDTO updateUserDTO);

    void delete(int id);
}
