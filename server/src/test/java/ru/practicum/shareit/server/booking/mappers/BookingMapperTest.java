package ru.practicum.shareit.server.booking.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.common.dto.ResponseBookingDTO;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

class BookingMapperTest {

    BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toResponseDto_nullBooking_thenReturnNull() {
        ResponseBookingDTO result = mapper.toResponseDto(null);

        Assertions.assertNull(result);
    }

    @Test
    void toResponseDto_thenMapFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        User booker = User.builder()
                .id(1)
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(100)
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .build();

        Booking booking = Booking.builder()
                .id(5)
                .booker(booker)
                .item(item)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        ResponseBookingDTO result = mapper.toResponseDto(booking);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.getId());
        Assertions.assertEquals(start, result.getStart());
        Assertions.assertEquals(end, result.getEnd());
        Assertions.assertEquals(BookingStatus.WAITING.toString(), result.getStatus());

        Assertions.assertNotNull(result.getBooker());
        Assertions.assertEquals(1, result.getBooker().getId());
        Assertions.assertEquals("Иван", result.getBooker().getName());
        Assertions.assertEquals("ivan@test.ru", result.getBooker().getEmail());

        Assertions.assertNotNull(result.getItem());
        Assertions.assertEquals(10, result.getItem().getId());
        Assertions.assertEquals("Дрель", result.getItem().getName());
        Assertions.assertEquals("Мощная дрель", result.getItem().getDescription());
        Assertions.assertTrue(result.getItem().isAvailable());

        Assertions.assertNull(result.getItem().getLastBooking());
        Assertions.assertNull(result.getItem().getNextBooking());
        Assertions.assertNull(result.getItem().getComments());
    }

    @Test
    void toEntity_thenMapFieldsAndSetWaitingStatus() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(start)
                .end(end)
                .build();

        User booker = User.builder()
                .id(1)
                .name("Петр")
                .email("petr@test.ru")
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(100)
                .name("Лестница")
                .description("Высокая лестница")
                .available(true)
                .build();

        Booking result = mapper.toEntity(dto, booker, item);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getId());
        Assertions.assertEquals(start, result.getStart());
        Assertions.assertEquals(end, result.getEnd());
        Assertions.assertEquals(booker, result.getBooker());
        Assertions.assertEquals(item, result.getItem());
        Assertions.assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void toEntity_nullDto_thenReturnBookingWithBookerAndItem() {
        User booker = User.builder()
                .id(1)
                .name("Анна")
                .email("anna@test.ru")
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(100)
                .name("Шуруповерт")
                .description("Удобный шуруповерт")
                .available(true)
                .build();

        Booking result = mapper.toEntity(null, booker, item);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getId());
        Assertions.assertNull(result.getStart());
        Assertions.assertNull(result.getEnd());
        Assertions.assertEquals(booker, result.getBooker());
        Assertions.assertEquals(item, result.getItem());
        Assertions.assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void toEntity_allNull_thenReturnNull() {
        Booking result = mapper.toEntity(null, null, null);

        Assertions.assertNull(result);
    }
}