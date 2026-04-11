package ru.practicum.shareit.server.user.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.common.dto.ResponseUserDTO;
import ru.practicum.shareit.server.user.model.User;

class UserMapperTest {

    UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toResponseDto_nullUser_thenReturnNull() {
        ResponseUserDTO result = mapper.toResponseDto(null);

        Assertions.assertNull(result);
    }

    @Test
    void toResponseDto_thenMapFields() {
        User user = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        ResponseUserDTO result = mapper.toResponseDto(user);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Иван", result.getName());
        Assertions.assertEquals("ivan@test.ru", result.getEmail());
    }

    @Test
    void toEntity_thenMapFieldsAndIgnoreId() {
        CreateUserDTO dto = CreateUserDTO.builder()
                .name("Мария")
                .email("maria@test.ru")
                .build();

        User result = mapper.toEntity(dto);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getId());
        Assertions.assertEquals("Мария", result.getName());
        Assertions.assertEquals("maria@test.ru", result.getEmail());
    }

    @Test
    void toEntity_nullDto_thenReturnNull() {
        User result = mapper.toEntity(null);

        Assertions.assertNull(result);
    }

    @Test
    void update_thenChangeOnlyNonNullFields() {
        User user = User.builder()
                .id(5)
                .name("Старое имя")
                .email("old@test.ru")
                .build();

        UpdateUserDTO dto = UpdateUserDTO.builder()
                .name("Новое имя")
                .email(null)
                .build();

        mapper.update(user, dto);

        Assertions.assertEquals(5, user.getId());
        Assertions.assertEquals("Новое имя", user.getName());
        Assertions.assertEquals("old@test.ru", user.getEmail());
    }

    @Test
    void update_withEmailOnly_thenChangeOnlyEmail() {
        User user = User.builder()
                .id(7)
                .name("Имя")
                .email("old@test.ru")
                .build();

        UpdateUserDTO dto = UpdateUserDTO.builder()
                .name(null)
                .email("new@test.ru")
                .build();

        mapper.update(user, dto);

        Assertions.assertEquals(7, user.getId());
        Assertions.assertEquals("Имя", user.getName());
        Assertions.assertEquals("new@test.ru", user.getEmail());
    }

    @Test
    void update_nullDto_thenDoNothing() {
        User user = User.builder()
                .id(9)
                .name("Имя")
                .email("mail@test.ru")
                .build();

        mapper.update(user, null);

        Assertions.assertEquals(9, user.getId());
        Assertions.assertEquals("Имя", user.getName());
        Assertions.assertEquals("mail@test.ru", user.getEmail());
    }

    @Test
    void update_allFieldsNull_thenDoNothing() {
        User user = User.builder()
                .id(11)
                .name("Имя")
                .email("mail@test.ru")
                .build();

        UpdateUserDTO dto = UpdateUserDTO.builder()
                .name(null)
                .email(null)
                .build();

        mapper.update(user, dto);

        Assertions.assertEquals(11, user.getId());
        Assertions.assertEquals("Имя", user.getName());
        Assertions.assertEquals("mail@test.ru", user.getEmail());
    }
}