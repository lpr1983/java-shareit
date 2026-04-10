package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.server.booking.mappers.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.storage.BookingStorage;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    ItemStorage itemStorage;

    @Mock
    UserStorage userStorage;

    @Mock
    BookingStorage bookingStorage;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void create_startAfterEnd_thenThrowValidation() {
        LocalDateTime now = LocalDateTime.now();
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(now.plusDays(2))
                .end(now.plusDays(1))
                .build();

        Assertions.assertThrows(ValidationException.class, () -> bookingService.create(dto, 1));

        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingStorage, bookingMapper);
    }

    @Test
    void create_userNotFound_thenThrowNotFound() {
        LocalDateTime now = LocalDateTime.now();
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.create(dto, 1));

        Mockito.verify(userStorage).findById(1);
        Mockito.verifyNoMoreInteractions(userStorage);
        Mockito.verifyNoInteractions(itemStorage, bookingStorage, bookingMapper);
    }

    @Test
    void create_itemNotFound_thenThrowNotFound() {
        LocalDateTime now = LocalDateTime.now();
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(User.builder().id(1).build()));
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.create(dto, 1));

        Mockito.verify(userStorage).findById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verifyNoInteractions(bookingStorage, bookingMapper);
    }

    @Test
    void create_itemUnavailable_thenThrowValidation() {
        LocalDateTime now = LocalDateTime.now();
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(2)
                .name("дрель")
                .description("мощная дрель")
                .available(false)
                .build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(User.builder().id(1).build()));
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.of(item));

        Assertions.assertThrows(ValidationException.class, () -> bookingService.create(dto, 1));

        Mockito.verify(userStorage).findById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verifyNoInteractions(bookingStorage, bookingMapper);
    }

    @Test
    void create_bookOwnItem_thenThrowValidation() {
        LocalDateTime now = LocalDateTime.now();
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        Item item = Item.builder()
                .id(10)
                .ownerId(1)
                .name("дрель")
                .description("мощная дрель")
                .available(true)
                .build();

        Mockito.when(userStorage.findById(1)).thenReturn(Optional.of(User.builder().id(1).build()));
        Mockito.when(itemStorage.findById(10)).thenReturn(Optional.of(item));

        Assertions.assertThrows(ValidationException.class, () -> bookingService.create(dto, 1));

        Mockito.verify(userStorage).findById(1);
        Mockito.verify(itemStorage).findById(10);
        Mockito.verifyNoInteractions(bookingStorage, bookingMapper);
    }

    @Test
    void approve_bookingNotFound_thenThrowNotFound() {
        Mockito.when(bookingStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.approve(10, 1, true));

        Mockito.verify(bookingStorage).findById(10);
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingMapper);
    }

    @Test
    void approve_userNotOwner_thenThrowValidation() {
        Booking booking = Booking.builder()
                .id(10)
                .item(Item.builder().id(20).ownerId(2).build())
                .status(BookingStatus.WAITING)
                .build();

        Mockito.when(bookingStorage.findById(10)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class, () -> bookingService.approve(10, 1, true));

        Mockito.verify(bookingStorage).findById(10);
        Mockito.verify(bookingStorage, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingMapper);
    }

    @Test
    void approve_statusNotWaiting_thenThrowValidation() {
        Booking booking = Booking.builder()
                .id(10)
                .item(Item.builder().id(20).ownerId(1).build())
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(bookingStorage.findById(10)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class, () -> bookingService.approve(10, 1, true));

        Mockito.verify(bookingStorage).findById(10);
        Mockito.verify(bookingStorage, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingMapper);
    }

    @Test
    void getById_bookingNotFound_thenThrowNotFound() {
        Mockito.when(bookingStorage.findById(10)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getById(1, 10));

        Mockito.verify(bookingStorage).findById(10);
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingMapper);
    }

    @Test
    void getById_userIsNotBookerAndNotOwner_thenThrowValidation() {
        Booking booking = Booking.builder()
                .id(10)
                .booker(User.builder().id(2).build())
                .item(Item.builder().id(20).ownerId(3).build())
                .build();

        Mockito.when(bookingStorage.findById(10)).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class, () -> bookingService.getById(1, 10));

        Mockito.verify(bookingStorage).findById(10);
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingMapper);
    }

    @Test
    void getBookingsOfUser_userNotFound_thenThrowNotFound() {
        Mockito.when(userStorage.existsById(1)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBookingsOfUser(1, "ALL"));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verifyNoInteractions(itemStorage, bookingStorage, bookingMapper);
    }

    @Test
    void getBookingsOfUser_wrongState_thenThrowValidation() {
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getBookingsOfUser(1, "WRONG"));
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingStorage, bookingMapper);
    }

    @Test
    void getBookingsOfOwner_userNotFound_thenThrowNotFound() {
        Mockito.when(userStorage.existsById(1)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBookingsOfOwner(1, "ALL"));

        Mockito.verify(userStorage).existsById(1);
        Mockito.verifyNoInteractions(itemStorage, bookingStorage, bookingMapper);
    }

    @Test
    void getBookingsOfOwner_wrongState_thenThrowValidation() {
        Assertions.assertThrows(ValidationException.class, () -> bookingService.getBookingsOfOwner(1, "WRONG"));
        Mockito.verifyNoInteractions(userStorage, itemStorage, bookingStorage, bookingMapper);
    }
}