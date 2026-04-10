package ru.practicum.shareit.server.item.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.common.dto.ResponseItemDTO;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;

class ItemMapperTest {

    ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toResponseDto_nullItem_thenReturnNull() {
        ResponseItemDTO result = mapper.toResponseDto(null);

        Assertions.assertNull(result);
    }

    @Test
    void toResponseDto_thenMapFields() {
        Item item = new Item();
        item.setId(1);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);

        ResponseItemDTO result = mapper.toResponseDto(item);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Дрель", result.getName());
        Assertions.assertEquals("Мощная дрель", result.getDescription());
        Assertions.assertTrue(result.isAvailable());

        Assertions.assertNull(result.getLastBooking());
        Assertions.assertNull(result.getNextBooking());
        Assertions.assertNull(result.getComments());
    }

    @Test
    void toEntity_thenMapFieldsAndRequest() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("Шуруповерт")
                .description("Удобный шуруповерт")
                .available(true)
                .build();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10);
        itemRequest.setDescription("Нужен шуруповерт");

        Item result = mapper.toEntity(dto, itemRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Шуруповерт", result.getName());
        Assertions.assertEquals("Удобный шуруповерт", result.getDescription());
        Assertions.assertTrue(result.isAvailable());
        Assertions.assertEquals(itemRequest, result.getItemRequest());
    }

    @Test
    void update_thenChangeOnlyNonNullFields() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(77);

        Item item = new Item();
        item.setId(5);
        item.setOwnerId(100);
        item.setName("Старое имя");
        item.setDescription("Старое описание");
        item.setAvailable(true);
        item.setItemRequest(itemRequest);

        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("Новое имя")
                .description(null)
                .available(false)
                .build();

        mapper.update(item, dto);

        Assertions.assertEquals(5, item.getId());
        Assertions.assertEquals(100, item.getOwnerId());
        Assertions.assertEquals("Новое имя", item.getName());
        Assertions.assertEquals("Старое описание", item.getDescription());
        Assertions.assertFalse(item.isAvailable());
        Assertions.assertEquals(itemRequest, item.getItemRequest());
    }

    @Test
    void update_nullDto_thenDoNothing() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(77);

        Item item = new Item();
        item.setId(5);
        item.setOwnerId(100);
        item.setName("Имя");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setItemRequest(itemRequest);

        mapper.update(item, null);

        Assertions.assertEquals(5, item.getId());
        Assertions.assertEquals(100, item.getOwnerId());
        Assertions.assertEquals("Имя", item.getName());
        Assertions.assertEquals("Описание", item.getDescription());
        Assertions.assertTrue(item.isAvailable());
        Assertions.assertEquals(itemRequest, item.getItemRequest());
    }
}