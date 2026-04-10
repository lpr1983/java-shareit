package ru.practicum.shareit.server.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.storage.BookingStorage;
import ru.practicum.shareit.common.dto.ResponseCommentDTO;
import ru.practicum.shareit.common.dto.ResponseItemDTO;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.CommentStorage;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.storage.ItemRequestStorage;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemStorage itemStorage;

    @Autowired
    UserStorage userStorage;

    @Autowired
    BookingStorage bookingStorage;

    @Autowired
    CommentStorage commentStorage;

    @Autowired
    ItemRequestStorage itemRequestStorage;

    @Test
    void create_thenSaveItemWithRequestInDb() {
        User owner = new User();
        owner.setName("Иван");
        owner.setEmail("ivan_item_create@test.ru");
        owner = userStorage.save(owner);

        User requester = new User();
        requester.setName("Петр");
        requester.setEmail("petr_item_request@test.ru");
        requester = userStorage.save(requester);

        ItemRequest request = new ItemRequest();
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.now());
        request.setUser(requester);
        request = itemRequestStorage.save(request);

        CreateItemDTO dto = new CreateItemDTO();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        dto.setRequestId(request.getId());

        ResponseItemDTO result = itemService.create(dto, owner.getId());

        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertEquals("Дрель", result.getName());
        Assertions.assertEquals("Мощная дрель", result.getDescription());
        Assertions.assertTrue(result.isAvailable());

        Optional<Item> savedItemOpt = itemStorage.findById(result.getId());
        Assertions.assertTrue(savedItemOpt.isPresent());

        Item savedItem = savedItemOpt.get();
        Assertions.assertEquals(owner.getId(), savedItem.getOwnerId());
        Assertions.assertEquals("Дрель", savedItem.getName());
        Assertions.assertEquals("Мощная дрель", savedItem.getDescription());
        Assertions.assertTrue(savedItem.isAvailable());
        Assertions.assertNotNull(savedItem.getItemRequest());
        Assertions.assertEquals(request.getId(), savedItem.getItemRequest().getId());
    }

    @Test
    void update_thenUpdateItemInDb() {
        User owner = new User();
        owner.setName("Мария");
        owner.setEmail("maria_item_update@test.ru");
        owner = userStorage.save(owner);

        Item item = new Item();
        item.setOwnerId(owner.getId());
        item.setName("Старое имя");
        item.setDescription("Старое описание");
        item.setAvailable(true);
        item = itemStorage.save(item);

        UpdateItemDTO dto = new UpdateItemDTO();
        dto.setName("Новое имя");
        dto.setDescription("Новое описание");
        dto.setAvailable(false);

        ResponseItemDTO result = itemService.update(dto, item.getId(), owner.getId());

        Assertions.assertEquals(item.getId(), result.getId());
        Assertions.assertEquals("Новое имя", result.getName());
        Assertions.assertEquals("Новое описание", result.getDescription());
        Assertions.assertFalse(result.isAvailable());

        Optional<Item> updatedItemOpt = itemStorage.findById(item.getId());
        Assertions.assertTrue(updatedItemOpt.isPresent());

        Item updatedItem = updatedItemOpt.get();
        Assertions.assertEquals("Новое имя", updatedItem.getName());
        Assertions.assertEquals("Новое описание", updatedItem.getDescription());
        Assertions.assertFalse(updatedItem.isAvailable());
    }

    @Test
    void search_thenReturnOnlyAvailableMatchingItems() {
        User owner = new User();
        owner.setName("Сергей");
        owner.setEmail("sergey_item_search@test.ru");
        owner = userStorage.save(owner);

        Item item1 = new Item();
        item1.setOwnerId(owner.getId());
        item1.setName("Дрель");
        item1.setDescription("Мощная дрель для ремонта");
        item1.setAvailable(true);
        itemStorage.save(item1);

        Item item2 = new Item();
        item2.setOwnerId(owner.getId());
        item2.setName("Дрель скрытая");
        item2.setDescription("Недоступная дрель");
        item2.setAvailable(false);
        itemStorage.save(item2);

        Item item3 = new Item();
        item3.setOwnerId(owner.getId());
        item3.setName("Пила");
        item3.setDescription("Острая пила");
        item3.setAvailable(true);
        itemStorage.save(item3);

        List<ResponseItemDTO> result = itemService.search("дрель");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Дрель", result.get(0).getName());
        Assertions.assertTrue(result.get(0).isAvailable());
    }

    @Test
    void createComment_thenSaveCommentInDb() {
        User owner = new User();
        owner.setName("Олег");
        owner.setEmail("oleg_item_owner@test.ru");
        owner = userStorage.save(owner);

        User booker = new User();
        booker.setName("Анна");
        booker.setEmail("anna_item_booker@test.ru");
        booker = userStorage.save(booker);

        Item item = new Item();
        item.setOwnerId(owner.getId());
        item.setName("Шуруповерт");
        item.setDescription("Удобный шуруповерт");
        item.setAvailable(true);
        item = itemStorage.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingStorage.save(booking);

        CreateCommentDTO dto = new CreateCommentDTO();
        dto.setText("Очень удобная вещь");

        ResponseCommentDTO result = itemService.createComment(dto, booker.getId(), item.getId());

        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertEquals("Очень удобная вещь", result.getText());
        Assertions.assertEquals("Анна", result.getAuthorName());
        Assertions.assertNotNull(result.getCreated());

        List<Comment> savedComments = commentStorage.findAll();
        Assertions.assertEquals(1, savedComments.size());
        Assertions.assertEquals("Очень удобная вещь", savedComments.get(0).getText());
        Assertions.assertEquals(booker.getId(), savedComments.get(0).getAuthor().getId());
        Assertions.assertEquals(item.getId(), savedComments.get(0).getItem().getId());
    }

    @Test
    void getById_owner_thenReturnItemWithCommentsAndBookingDates() {
        User owner = new User();
        owner.setName("Виктор");
        owner.setEmail("victor_item_owner@test.ru");
        owner = userStorage.save(owner);

        User pastBooker = new User();
        pastBooker.setName("Ирина");
        pastBooker.setEmail("irina_past@test.ru");
        pastBooker = userStorage.save(pastBooker);

        User futureBooker = new User();
        futureBooker.setName("Дмитрий");
        futureBooker.setEmail("dmitry_future@test.ru");
        futureBooker = userStorage.save(futureBooker);

        Item item = new Item();
        item.setOwnerId(owner.getId());
        item.setName("Лестница");
        item.setDescription("Высокая лестница");
        item.setAvailable(true);
        item = itemStorage.save(item);

        LocalDateTime pastStart = LocalDateTime.now().minusDays(5);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(3);

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(pastBooker);
        pastBooking.setStart(pastStart);
        pastBooking.setEnd(pastEnd);
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingStorage.save(pastBooking);

        LocalDateTime futureStart = LocalDateTime.now().plusDays(2);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(4);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(futureBooker);
        futureBooking.setStart(futureStart);
        futureBooking.setEnd(futureEnd);
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingStorage.save(futureBooking);

        Comment comment = new Comment();
        comment.setText("Очень пригодилась");
        comment.setItem(item);
        comment.setAuthor(pastBooker);
        comment.setCreated(LocalDateTime.now().minusDays(1));
        commentStorage.save(comment);

        ResponseItemDTO result = itemService.getById(item.getId(), owner.getId());

        Assertions.assertEquals(item.getId(), result.getId());
        Assertions.assertEquals("Лестница", result.getName());
        Assertions.assertEquals("Высокая лестница", result.getDescription());
        Assertions.assertTrue(result.isAvailable());

        Assertions.assertEquals(pastStart.withNano(0), result.getLastBooking().withNano(0));
        Assertions.assertEquals(futureStart.withNano(0), result.getNextBooking().withNano(0));

        Assertions.assertNotNull(result.getComments());
        Assertions.assertEquals(1, result.getComments().size());
        Assertions.assertEquals("Очень пригодилась", result.getComments().get(0).getText());
        Assertions.assertEquals("Ирина", result.getComments().get(0).getAuthorName());
    }
}