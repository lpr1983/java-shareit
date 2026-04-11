package ru.practicum.shareit.server.item.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.ResponseCommentDTO;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

class CommentMapperTest {

    CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toResponseDto_nullComment_thenReturnNull() {
        ResponseCommentDTO result = mapper.toResponseDto(null);

        Assertions.assertNull(result);
    }

    @Test
    void toResponseDto_thenMapFields() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        User author = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        Comment comment = new Comment();
        comment.setId(10);
        comment.setText("Очень полезная вещь");
        comment.setCreated(created);
        comment.setAuthor(author);

        ResponseCommentDTO result = mapper.toResponseDto(comment);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(10, result.getId());
        Assertions.assertEquals("Очень полезная вещь", result.getText());
        Assertions.assertEquals(created, result.getCreated());
        Assertions.assertEquals("Иван", result.getAuthorName());
    }

    @Test
    void toResponseDto_nullAuthor_thenReturnNullAuthorName() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        Comment comment = new Comment();
        comment.setId(11);
        comment.setText("Комментарий без автора");
        comment.setCreated(created);
        comment.setAuthor(null);

        ResponseCommentDTO result = mapper.toResponseDto(comment);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(11, result.getId());
        Assertions.assertEquals("Комментарий без автора", result.getText());
        Assertions.assertEquals(created, result.getCreated());
        Assertions.assertNull(result.getAuthorName());
    }

    @Test
    void toResponseDto_authorNameIsNull_thenReturnNullAuthorName() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        User author = User.builder()
                .id(1)
                .name(null)
                .email("noname@test.ru")
                .build();

        Comment comment = new Comment();
        comment.setId(12);
        comment.setText("Комментарий")
        ;
        comment.setCreated(created);
        comment.setAuthor(author);

        ResponseCommentDTO result = mapper.toResponseDto(comment);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(12, result.getId());
        Assertions.assertEquals("Комментарий", result.getText());
        Assertions.assertEquals(created, result.getCreated());
        Assertions.assertNull(result.getAuthorName());
    }

    @Test
    void toEntity_thenMapFields() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("Очень полезная вещь")
                .build();

        User author = User.builder()
                .id(1)
                .name("Петр")
                .email("petr@test.ru")
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(100)
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .build();

        Comment result = mapper.toEntity(dto, author, item, created);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getId());
        Assertions.assertEquals("Очень полезная вещь", result.getText());
        Assertions.assertEquals(author, result.getAuthor());
        Assertions.assertEquals(item, result.getItem());
        Assertions.assertEquals(created, result.getCreated());
    }

    @Test
    void toEntity_nullDto_thenReturnCommentWithOtherFields() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        User author = User.builder()
                .id(1)
                .name("Мария")
                .email("maria@test.ru")
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(100)
                .name("Лестница")
                .description("Высокая лестница")
                .available(true)
                .build();

        Comment result = mapper.toEntity(null, author, item, created);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getId());
        Assertions.assertNull(result.getText());
        Assertions.assertEquals(author, result.getAuthor());
        Assertions.assertEquals(item, result.getItem());
        Assertions.assertEquals(created, result.getCreated());
    }

    @Test
    void toEntity_allNull_thenReturnNull() {
        Comment result = mapper.toEntity(null, null, null, null);

        Assertions.assertNull(result);
    }
}