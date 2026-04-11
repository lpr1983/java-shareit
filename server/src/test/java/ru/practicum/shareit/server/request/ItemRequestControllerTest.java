package ru.practicum.shareit.server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.error.handler.ErrorHandler;
import ru.practicum.shareit.common.dto.ItemRequestDTO;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Test
    @SneakyThrows
    void create() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("Нужна дрель")
                .build();

        ItemRequestDTO dtoResponse = ItemRequestDTO.builder()
                .id(1)
                .description("Нужна дрель")
                .build();

        Mockito.when(itemRequestService.create(any(CreateItemRequestDTO.class), eq(5)))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 5)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));

        ArgumentCaptor<CreateItemRequestDTO> captor = ArgumentCaptor.forClass(CreateItemRequestDTO.class);
        Mockito.verify(itemRequestService).create(captor.capture(), eq(5));

        Assertions.assertEquals("Нужна дрель", captor.getValue().getDescription());
    }

    @Test
    @SneakyThrows
    void create_notFound_thenReturnNotFound() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("Нужна дрель")
                .build();

        Mockito.when(itemRequestService.create(any(CreateItemRequestDTO.class), eq(5)))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 5"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 5)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с id: 5"));
    }

    @Test
    @SneakyThrows
    void getMyRequests() {
        List<ItemRequestDTO> dtoList = List.of(
                ItemRequestDTO.builder().id(1).description("Нужна дрель").build(),
                ItemRequestDTO.builder().id(2).description("Нужна пила").build()
        );

        Mockito.when(itemRequestService.getMyRequests(5))
                .thenReturn(dtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Нужна пила"));

        Mockito.verify(itemRequestService).getMyRequests(5);
    }

    @Test
    @SneakyThrows
    void getMyRequests_notFound_thenReturnNotFound() {
        Mockito.when(itemRequestService.getMyRequests(5))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 5"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с id: 5"));
    }

    @Test
    @SneakyThrows
    void getAllRequestsExceptMy() {
        List<ItemRequestDTO> dtoList = List.of(
                ItemRequestDTO.builder().id(3).description("Нужен шуруповерт").build(),
                ItemRequestDTO.builder().id(4).description("Нужна лестница").build()
        );

        Mockito.when(itemRequestService.getAllRequestsExceptMy(5, 0, 50))
                .thenReturn(dtoList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].description").value("Нужен шуруповерт"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].description").value("Нужна лестница"));

        Mockito.verify(itemRequestService).getAllRequestsExceptMy(5, 0, 50);
    }

    @Test
    @SneakyThrows
    void getAllRequestsExceptMy_withPaging() {
        List<ItemRequestDTO> dtoList = List.of(
                ItemRequestDTO.builder().id(3).description("Нужен шуруповерт").build()
        );

        Mockito.when(itemRequestService.getAllRequestsExceptMy(5, 10, 5))
                .thenReturn(dtoList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 5)
                        .param("from", "10")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].description").value("Нужен шуруповерт"));

        Mockito.verify(itemRequestService).getAllRequestsExceptMy(5, 10, 5);
    }

    @Test
    @SneakyThrows
    void getAllRequestsExceptMy_notFound_thenReturnNotFound() {
        Mockito.when(itemRequestService.getAllRequestsExceptMy(5, 0, 50))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 5"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с id: 5"));
    }

    @Test
    @SneakyThrows
    void getById_success_thenReturnOk() {
        ItemRequestDTO dtoResponse = ItemRequestDTO.builder()
                .id(1)
                .description("Нужна дрель")
                .build();

        Mockito.when(itemRequestService.getById(1))
                .thenReturn(dtoResponse);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));

        Mockito.verify(itemRequestService).getById(1);
    }

    @Test
    @SneakyThrows
    void getById_notFound_thenReturnNotFound() {
        Mockito.when(itemRequestService.getById(1))
                .thenThrow(new NotFoundException("Не найден запрос с id: 1"));

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден запрос с id: 1"));
    }
}