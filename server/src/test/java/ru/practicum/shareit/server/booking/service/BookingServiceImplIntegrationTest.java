package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.common.dto.ResponseBookingDTO;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.storage.BookingStorage;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingStorage bookingStorage;

    @Autowired
    UserStorage userStorage;

    @Autowired
    ItemStorage itemStorage;

    @Test
    void create_thenSaveBookingInDb() {
        User owner = User.builder()
                .name("Владелец")
                .email("owner_booking_create@test.ru")
                .build();
        owner = userStorage.save(owner);

        User booker = User.builder()
                .name("Арендатор")
                .email("booker_booking_create@test.ru")
                .build();
        booker = userStorage.save(booker);

        Item item = Item.builder()
                .ownerId(owner.getId())
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .build();
        item = itemStorage.save(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .build();

        ResponseBookingDTO result = bookingService.create(dto, booker.getId());

        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertEquals(BookingStatus.WAITING.toString(), result.getStatus());
        Assertions.assertEquals(item.getId(), result.getItem().getId());
        Assertions.assertEquals(booker.getId(), result.getBooker().getId());
        Assertions.assertEquals(start.withNano(0), result.getStart().withNano(0));
        Assertions.assertEquals(end.withNano(0), result.getEnd().withNano(0));

        Optional<Booking> savedBookingOpt = bookingStorage.findById(result.getId());
        Assertions.assertTrue(savedBookingOpt.isPresent());

        Booking savedBooking = savedBookingOpt.get();
        Assertions.assertEquals(item.getId(), savedBooking.getItem().getId());
        Assertions.assertEquals(booker.getId(), savedBooking.getBooker().getId());
        Assertions.assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        Assertions.assertEquals(start.withNano(0), savedBooking.getStart().withNano(0));
        Assertions.assertEquals(end.withNano(0), savedBooking.getEnd().withNano(0));
    }

    @Test
    void approve_thenUpdateBookingStatusInDb() {
        User owner = User.builder()
                .name("Владелец")
                .email("owner_booking_approve@test.ru")
                .build();
        owner = userStorage.save(owner);

        User booker = User.builder()
                .name("Арендатор")
                .email("booker_booking_approve@test.ru")
                .build();
        booker = userStorage.save(booker);

        Item item = Item.builder()
                .ownerId(owner.getId())
                .name("Лестница")
                .description("Высокая лестница")
                .available(true)
                .build();
        item = itemStorage.save(item);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1).withNano(0))
                .end(LocalDateTime.now().plusDays(2).withNano(0))
                .status(BookingStatus.WAITING)
                .build();
        booking = bookingStorage.save(booking);

        ResponseBookingDTO result = bookingService.approve(booking.getId(), owner.getId(), true);

        Assertions.assertEquals(booking.getId(), result.getId());
        Assertions.assertEquals(BookingStatus.APPROVED.toString(), result.getStatus());

        Booking savedBooking = bookingStorage.findById(booking.getId()).orElseThrow();
        Assertions.assertEquals(BookingStatus.APPROVED, savedBooking.getStatus());
    }

    @Test
    void getById_thenReturnBooking() {
        User owner = User.builder()
                .name("Владелец")
                .email("owner_booking_getbyid@test.ru")
                .build();
        owner = userStorage.save(owner);

        User booker = User.builder()
                .name("Арендатор")
                .email("booker_booking_getbyid@test.ru")
                .build();
        booker = userStorage.save(booker);

        Item item = Item.builder()
                .ownerId(owner.getId())
                .name("Шуруповерт")
                .description("Удобный шуруповерт")
                .available(true)
                .build();
        item = itemStorage.save(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(2).withNano(0);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();
        booking = bookingStorage.save(booking);

        ResponseBookingDTO result = bookingService.getById(booker.getId(), booking.getId());

        Assertions.assertEquals(booking.getId(), result.getId());
        Assertions.assertEquals(item.getId(), result.getItem().getId());
        Assertions.assertEquals(booker.getId(), result.getBooker().getId());
        Assertions.assertEquals(BookingStatus.WAITING.toString(), result.getStatus());
        Assertions.assertEquals(start.withNano(0), result.getStart().withNano(0));
        Assertions.assertEquals(end.withNano(0), result.getEnd().withNano(0));
    }

    @Test
    void getBookingsOfUser_all_thenReturnUserBookings() {
        User owner = User.builder()
                .name("Владелец")
                .email("owner_booking_user_all@test.ru")
                .build();
        owner = userStorage.save(owner);

        User booker = User.builder()
                .name("Арендатор")
                .email("booker_booking_user_all@test.ru")
                .build();
        booker = userStorage.save(booker);

        Item item1 = Item.builder()
                .ownerId(owner.getId())
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .build();

        item1 = itemStorage.save(item1);

        Item item2 = Item.builder()
                .ownerId(owner.getId())
                .name("Пила")
                .description("Острая пила")
                .available(true)
                .build();
        item2 = itemStorage.save(item2);

        Booking booking1 = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5).withNano(0))
                .end(LocalDateTime.now().minusDays(3).withNano(0))
                .status(BookingStatus.APPROVED)
                .build();
        bookingStorage.save(booking1);

        Booking booking2 = Booking.builder()
                .item(item2)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(2).withNano(0))
                .end(LocalDateTime.now().plusDays(4).withNano(0))
                .status(BookingStatus.WAITING)
                .build();
        bookingStorage.save(booking2);

        List<ResponseBookingDTO> result = bookingService.getBookingsOfUser(booker.getId(), "ALL");

        int item1Id = item1.getId();
        int item2Id = item2.getId();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().anyMatch(b -> b.getItem().getId() == item1Id));
        Assertions.assertTrue(result.stream().anyMatch(b -> b.getItem().getId() == item2Id));
    }

    @Test
    void getBookingsOfOwner_all_thenReturnOwnerBookings() {
        User owner = User.builder()
                .name("Владелец")
                .email("owner_booking_owner_all@test.ru")
                .build();
        owner = userStorage.save(owner);

        User booker1 = User.builder()
                .name("Арендатор1")
                .email("booker1_booking_owner_all@test.ru")
                .build();
        booker1 = userStorage.save(booker1);

        User booker2 = User.builder()
                .name("Арендатор2")
                .email("booker2_booking_owner_all@test.ru")
                .build();
        booker2 = userStorage.save(booker2);

        Item item1 = Item.builder()
                .ownerId(owner.getId())
                .name("Лестница")
                .description("Высокая лестница")
                .available(true)
                .build();
        item1 = itemStorage.save(item1);

        Item item2 = Item.builder()
                .ownerId(owner.getId())
                .name("Шуруповерт")
                .description("Удобный шуруповерт")
                .available(true)
                .build();
        item2 = itemStorage.save(item2);

        Booking booking1 = Booking.builder()
                .item(item1)
                .booker(booker1)
                .start(LocalDateTime.now().minusDays(4).withNano(0))
                .end(LocalDateTime.now().minusDays(2).withNano(0))
                .status(BookingStatus.APPROVED)
                .build();
        bookingStorage.save(booking1);

        Booking booking2 = Booking.builder()
                .item(item2)
                .booker(booker2)
                .start(LocalDateTime.now().plusDays(3).withNano(0))
                .end(LocalDateTime.now().plusDays(5).withNano(0))
                .status(BookingStatus.WAITING)
                .build();
        bookingStorage.save(booking2);

        List<ResponseBookingDTO> result = bookingService.getBookingsOfOwner(owner.getId(), "ALL");

        int item1Id = item1.getId();
        int item2Id = item2.getId();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().anyMatch(b -> b.getItem().getId() == item1Id));
        Assertions.assertTrue(result.stream().anyMatch(b -> b.getItem().getId() == item2Id));
    }
}