package ru.practicum.shareit.server.request.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.common.dto.ItemRequestDTO;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class ItemRequestMapperTest {

    ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void toEntity() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder().description("test").build();
        LocalDateTime now = LocalDateTime.now();
        int userId = 1;
        User user = User.builder().id(userId).build();

        ItemRequest itemRequest = mapper.toEntity(dto, user, now);
        Assertions.assertNotNull(itemRequest.getUser());
        Assertions.assertEquals(itemRequest.getUser().getId(), userId);
        Assertions.assertEquals(itemRequest.getDescription(), "test");
        Assertions.assertEquals(itemRequest.getCreated(), now);
    }

    @Test
    void toResponseDto_allFields_thenMapCorrectly() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Нужна дрель");
        itemRequest.setCreated(created);

        Item item1 = new Item();
        item1.setId(10);
        item1.setName("Дрель");
        item1.setOwnerId(100);
        item1.setDescription("Мощная дрель");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(11);
        item2.setName("Шуруповерт");
        item2.setOwnerId(101);
        item2.setDescription("Удобный шуруповерт");
        item2.setAvailable(false);

        ItemRequestDTO result = mapper.toResponseDto(itemRequest, List.of(item1, item2));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Нужна дрель", result.getDescription());
        Assertions.assertEquals(created, result.getCreated());

        Assertions.assertNotNull(result.getItems());
        Assertions.assertEquals(2, result.getItems().size());

        Assertions.assertEquals(10, result.getItems().get(0).getId());
        Assertions.assertEquals("Дрель", result.getItems().get(0).getName());
        Assertions.assertEquals(100, result.getItems().get(0).getOwnerId());

        Assertions.assertEquals(11, result.getItems().get(1).getId());
        Assertions.assertEquals("Шуруповерт", result.getItems().get(1).getName());
        Assertions.assertEquals(101, result.getItems().get(1).getOwnerId());
    }

    @Test
    void toResponseDto_nullItems_thenReturnDtoWithNullItems() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(2);
        itemRequest.setDescription("Нужна пила");
        itemRequest.setCreated(created);

        ItemRequestDTO result = mapper.toResponseDto(itemRequest, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getId());
        Assertions.assertEquals("Нужна пила", result.getDescription());
        Assertions.assertEquals(created, result.getCreated());
        Assertions.assertNull(result.getItems());
    }

    @Test
    void toResponseDto_nullItemRequestAndItems_thenReturnNull() {
        ItemRequestDTO result = mapper.toResponseDto(null, null);

        Assertions.assertNull(result);
    }
}