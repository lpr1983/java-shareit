package ru.practicum.shareit.server.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.common.dto.ItemRequestDTO;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.storage.ItemRequestStorage;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    ItemRequestStorage itemRequestStorage;

    @Autowired
    ItemStorage itemStorage;

    @Autowired
    UserStorage userStorage;

    @Test
    void create_thenSaveItemRequestInDb() {
        User user = User.builder()
                .name("Иван")
                .email("ivan_request_create@test.ru")
                .build();
        user = userStorage.save(user);

        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("Нужна дрель")
                .build();

        ItemRequestDTO result = itemRequestService.create(dto, user.getId());

        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertEquals("Нужна дрель", result.getDescription());
        Assertions.assertNotNull(result.getCreated());

        Optional<ItemRequest> savedRequestOpt = itemRequestStorage.findById(result.getId());
        Assertions.assertTrue(savedRequestOpt.isPresent());

        ItemRequest savedRequest = savedRequestOpt.get();
        Assertions.assertEquals("Нужна дрель", savedRequest.getDescription());
        Assertions.assertEquals(user.getId(), savedRequest.getUser().getId());
        Assertions.assertNotNull(savedRequest.getCreated());
    }

    @Test
    void getMyRequests_thenReturnOwnRequestsWithItems() {
        User requester = User.builder()
                .name("test1")
                .email("test1@test.ru")
                .build();
        requester = userStorage.save(requester);

        User owner = User.builder()
                .name("test2")
                .email("test2@test.ru")
                .build();
        owner = userStorage.save(owner);

        ItemRequest oldRequest = new ItemRequest();
        oldRequest.setDescription("Нужна пила");
        oldRequest.setCreated(java.time.LocalDateTime.now().minusDays(2).withNano(0));
        oldRequest.setUser(requester);
        oldRequest = itemRequestStorage.save(oldRequest);

        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("Нужна дрель");
        newRequest.setCreated(java.time.LocalDateTime.now().minusDays(1).withNano(0));
        newRequest.setUser(requester);
        newRequest = itemRequestStorage.save(newRequest);

        Item item = new Item();
        item.setOwnerId(owner.getId());
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setItemRequest(newRequest);
        itemStorage.save(item);

        List<ItemRequestDTO> result = itemRequestService.getMyRequests(requester.getId());

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(newRequest.getId(), result.get(0).getId());
        Assertions.assertEquals("Нужна дрель", result.get(0).getDescription());
        Assertions.assertNotNull(result.get(0).getItems());
        Assertions.assertEquals(1, result.get(0).getItems().size());
        Assertions.assertEquals("Дрель", result.get(0).getItems().get(0).getName());
        Assertions.assertEquals(owner.getId(), result.get(0).getItems().get(0).getOwnerId());

        Assertions.assertEquals(oldRequest.getId(), result.get(1).getId());
        Assertions.assertEquals("Нужна пила", result.get(1).getDescription());
        Assertions.assertNotNull(result.get(1).getItems());
        Assertions.assertEquals(0, result.get(1).getItems().size());
    }

    @Test
    void getById_thenReturnRequestWithItems() {
        User requester = User.builder()
                .name("test1")
                .email("test1@test.ru")
                .build();
        requester = userStorage.save(requester);

        User owner = User.builder()
                .name("test2")
                .email("test2@test.ru")
                .build();
        owner = userStorage.save(owner);

        ItemRequest request = new ItemRequest();
        request.setDescription("Нужен шуруповерт");
        request.setCreated(java.time.LocalDateTime.now().withNano(0));
        request.setUser(requester);
        request = itemRequestStorage.save(request);

        Item item1 = new Item();
        item1.setOwnerId(owner.getId());
        item1.setName("Шуруповерт 1");
        item1.setDescription("Удобный шуруповерт");
        item1.setAvailable(true);
        item1.setItemRequest(request);
        item1 = itemStorage.save(item1);

        Item item2 = new Item();
        item2.setOwnerId(owner.getId());
        item2.setName("Шуруповерт 2");
        item2.setDescription("Еще один шуруповерт");
        item2.setAvailable(true);
        item2.setItemRequest(request);
        item2 = itemStorage.save(item2);

        ItemRequestDTO result = itemRequestService.getById(request.getId());

        Assertions.assertEquals(request.getId(), result.getId());
        Assertions.assertEquals("Нужен шуруповерт", result.getDescription());
        Assertions.assertNotNull(result.getItems());
        Assertions.assertEquals(2, result.getItems().size());

        Assertions.assertEquals(item1.getId(), result.getItems().get(0).getId());
        Assertions.assertEquals("Шуруповерт 1", result.getItems().get(0).getName());
        Assertions.assertEquals(owner.getId(), result.getItems().get(0).getOwnerId());

        Assertions.assertEquals(item2.getId(), result.getItems().get(1).getId());
        Assertions.assertEquals("Шуруповерт 2", result.getItems().get(1).getName());
        Assertions.assertEquals(owner.getId(), result.getItems().get(1).getOwnerId());
    }

    @Test
    void getAllRequestsExceptMy_thenReturnOnlyForeignRequests() {
        User me = User.builder()
                .name("Я")
                .email("me_request_all@test.ru")
                .build();
        me = userStorage.save(me);

        User other1 = User.builder()
                .name("Другой1")
                .email("other1@test.ru")
                .build();
        other1 = userStorage.save(other1);

        User other2 = User.builder()
                .name("Другой2")
                .email("other2@test.ru")
                .build();
        other2 = userStorage.save(other2);

        ItemRequest myRequest = new ItemRequest();
        myRequest.setDescription("Мой запрос");
        myRequest.setCreated(java.time.LocalDateTime.now().minusDays(3).withNano(0));
        myRequest.setUser(me);
        itemRequestStorage.save(myRequest);

        ItemRequest foreignRequest1 = new ItemRequest();
        foreignRequest1.setDescription("Чужой запрос 1");
        foreignRequest1.setCreated(java.time.LocalDateTime.now().minusDays(2).withNano(0));
        foreignRequest1.setUser(other1);
        foreignRequest1 = itemRequestStorage.save(foreignRequest1);

        ItemRequest foreignRequest2 = new ItemRequest();
        foreignRequest2.setDescription("Чужой запрос 2");
        foreignRequest2.setCreated(java.time.LocalDateTime.now().minusDays(1).withNano(0));
        foreignRequest2.setUser(other2);
        foreignRequest2 = itemRequestStorage.save(foreignRequest2);

        List<ItemRequestDTO> result = itemRequestService.getAllRequestsExceptMy(me.getId(), 0, 20);

        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(foreignRequest2.getId(), result.get(0).getId());
        Assertions.assertEquals("Чужой запрос 2", result.get(0).getDescription());

        Assertions.assertEquals(foreignRequest1.getId(), result.get(1).getId());
        Assertions.assertEquals("Чужой запрос 1", result.get(1).getDescription());

        Assertions.assertTrue(result.stream().noneMatch(r -> "Мой запрос".equals(r.getDescription())));
    }

    @Test
    void getAllRequestsExceptMy_withPaging_thenReturnRequestedSlice() {
        User me = User.builder()
                .name("Я")
                .email("test@test.ru")
                .build();
        me = userStorage.save(me);

        User other = User.builder()
                .name("Другой")
                .email("other_test@test.ru")
                .build();
        other = userStorage.save(other);

        for (int i = 1; i <= 5; i++) {
            ItemRequest request = new ItemRequest();
            request.setDescription("Запрос " + i);
            request.setCreated(java.time.LocalDateTime.now().minusDays(6 - i).withNano(0));
            request.setUser(other);
            itemRequestStorage.save(request);
        }

        List<ItemRequestDTO> result = itemRequestService.getAllRequestsExceptMy(me.getId(), 2, 2);

        Assertions.assertEquals(2, result.size());
    }
}