package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.ResponseUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    UserStorage userStorage;

    @Test
    void create_thenSaveUserInDb() {
        CreateUserDTO dto = CreateUserDTO.builder()
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        ResponseUserDTO result = userService.create(dto);

        Assertions.assertEquals("Иван", result.getName());
        Assertions.assertEquals("ivan@test.ru", result.getEmail());

        Optional<User> savedUserOpt = userStorage.findById(result.getId());
        Assertions.assertTrue(savedUserOpt.isPresent());

        User savedUser = savedUserOpt.get();
        Assertions.assertEquals("Иван", savedUser.getName());
        Assertions.assertEquals("ivan@test.ru", savedUser.getEmail());
    }

    @Test
    void getById_thenReturnSavedUser() {
        User user = User.builder()
                .name("Петр")
                .email("petr@test.ru")
                .build();
        user = userStorage.save(user);

        ResponseUserDTO result = userService.getById(user.getId());

        Assertions.assertEquals(user.getId(), result.getId());
        Assertions.assertEquals("Петр", result.getName());
        Assertions.assertEquals("petr@test.ru", result.getEmail());
    }

    @Test
    void update_thenUpdateUserInDb() {
        User user = User.builder()
                .name("Старое имя")
                .email("old@test.ru")
                .build();
        user = userStorage.save(user);

        UpdateUserDTO dto = UpdateUserDTO.builder()
                .name("Новое имя")
                .email("new@test.ru")
                .build();

        ResponseUserDTO result = userService.update(user.getId(), dto);

        Assertions.assertEquals(user.getId(), result.getId());
        Assertions.assertEquals("Новое имя", result.getName());
        Assertions.assertEquals("new@test.ru", result.getEmail());

        Optional<User> updatedUserOpt = userStorage.findById(user.getId());
        Assertions.assertTrue(updatedUserOpt.isPresent());

        User updatedUser = updatedUserOpt.get();
        Assertions.assertEquals("Новое имя", updatedUser.getName());
        Assertions.assertEquals("new@test.ru", updatedUser.getEmail());
    }

    @Test
    void getAll_thenReturnAllUsers() {
        User user1 = User.builder()
                .name("Иван")
                .email("ivan_all@test.ru")
                .build();

        User user2 = User.builder()
                .name("Мария")
                .email("maria_all@test.ru")
                .build();

        userStorage.save(user1);
        userStorage.save(user2);

        List<ResponseUserDTO> result = userService.getAll();

        Assertions.assertTrue(result.size() >= 2);

        boolean hasIvan = result.stream()
                .anyMatch(u -> "ivan_all@test.ru".equals(u.getEmail()) && "Иван".equals(u.getName()));

        boolean hasMaria = result.stream()
                .anyMatch(u -> "maria_all@test.ru".equals(u.getEmail()) && "Мария".equals(u.getName()));

        Assertions.assertTrue(hasIvan);
        Assertions.assertTrue(hasMaria);
    }

    @Test
    void delete_thenRemoveUserFromDb() {
        User user = User.builder()
                .name("Удаляемый")
                .email("delete_me@test.ru")
                .build();
        user = userStorage.save(user);

        userService.delete(user.getId());

        Optional<User> deletedUserOpt = userStorage.findById(user.getId());
        Assertions.assertTrue(deletedUserOpt.isEmpty());
    }
}