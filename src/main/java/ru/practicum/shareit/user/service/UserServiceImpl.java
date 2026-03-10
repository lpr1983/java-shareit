package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.AlgorithmFailException;
import ru.practicum.shareit.error.exception.ConflictException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDTO;
import ru.practicum.shareit.user.dto.UpdateUserDTO;
import ru.practicum.shareit.user.dto.ResponseUserDTO;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserServiceImpl(UserStorage userStorage,
                           UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseUserDTO getById(int id) {
        return userMapper.toResponseDto(
                checkUserExistsAndReturnIt(id)
        );
    }

    @Override
    public ResponseUserDTO create(CreateUserDTO createUserDTO) {
        log.info("create: {}", createUserDTO);

        String email = createUserDTO.getEmail();
        if (userStorage.emailExists(email)) {
            throw new ConflictException(String.format("Пользователь с email %s уже существует", email));
        }

        User user = userMapper.toEntity(createUserDTO);

        User createdUser = userStorage.create(user);

        return userMapper.toResponseDto(createdUser);
    }

    @Override
    public ResponseUserDTO update(int id, UpdateUserDTO updateUserDTO) {
        log.info("update: {}", updateUserDTO);
        User user = checkUserExistsAndReturnIt(id);

        String updateUserDTOEmail = updateUserDTO.getEmail();
        if (updateUserDTOEmail != null && !updateUserDTOEmail.equals(user.getEmail())) {
            checkEmailNotExists(updateUserDTOEmail);
        }

        userMapper.update(user, updateUserDTO);
        userStorage.update(user);

        User updatedUser = userStorage.getById(id)
                .orElseThrow(() -> new AlgorithmFailException("Не найден обновленный пользователь"));

        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public void delete(int id) {
        log.info("delete {}", id);

        checkUserExistsAndReturnIt(id);
        userStorage.delete(id);
    }

    private User checkUserExistsAndReturnIt(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + id));
    }

    private void checkEmailNotExists(String email) {
        if (userStorage.emailExists(email)) {
            throw new ConflictException(String.format("Пользователь с email %s уже существует", email));
        }
    }

}
