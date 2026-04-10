package ru.practicum.shareit.server.item;

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
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.error.handler.ErrorHandler;
import ru.practicum.shareit.common.dto.ResponseCommentDTO;
import ru.practicum.shareit.common.dto.ResponseItemDTO;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ItemController.class})
@Import(ErrorHandler.class)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Test
    @SneakyThrows
    void create() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("Дрель")
                .description("Мощная")
                .available(true)
                .requestId(7)
                .build();

        ResponseItemDTO dtoResponse = ResponseItemDTO.builder()
                .id(1)
                .name("Дрель")
                .description("Мощная")
                .available(true)
                .build();

        Mockito.when(itemService.create(any(CreateItemDTO.class), eq(10))).thenReturn(dtoResponse);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 10)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));

        ArgumentCaptor<CreateItemDTO> captor = ArgumentCaptor.forClass(CreateItemDTO.class);
        Mockito.verify(itemService).create(captor.capture(), eq(10));

        Assertions.assertEquals("Дрель", captor.getValue().getName());
        Assertions.assertEquals("Мощная", captor.getValue().getDescription());
        Assertions.assertEquals(true, captor.getValue().getAvailable());
        Assertions.assertEquals(7, captor.getValue().getRequestId());
    }

    @Test
    @SneakyThrows
    void update() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("updated")
                .description("updated desc")
                .available(false)
                .build();

        ResponseItemDTO dtoResponse = ResponseItemDTO.builder()
                .id(1)
                .name("updated")
                .description("updated desc")
                .available(false)
                .build();

        Mockito.when(itemService.update(
                any(UpdateItemDTO.class), eq(1), eq(10))).thenReturn(dtoResponse);

        mockMvc.perform(patch("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 10)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.available").value(false));

        ArgumentCaptor<UpdateItemDTO> captor = ArgumentCaptor.forClass(UpdateItemDTO.class);
        Mockito.verify(itemService).update(captor.capture(),eq(1), eq(10));

        Assertions.assertEquals("updated", captor.getValue().getName());
        Assertions.assertEquals("updated desc", captor.getValue().getDescription());
        Assertions.assertEquals(false, captor.getValue().getAvailable());
    }

    @Test
    @SneakyThrows
    void getById_success_thenReturnOk() {
        ResponseItemDTO dto = ResponseItemDTO.builder()
                .id(1)
                .name("Дрель")
                .build();

        Mockito.when(itemService.getById(1, 10)).thenReturn(dto);

        mockMvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));

        Mockito.verify(itemService).getById(1, 10);
    }

    @Test
    @SneakyThrows
    void getById_notFound_thenReturnNotFound() {
        Mockito.when(itemService.getById(1, 10)).thenThrow(new NotFoundException("item not found"));

        mockMvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("item not found"));
    }

    @Test
    @SneakyThrows
    void getItemsOfUser() {
        List<ResponseItemDTO> dtoList = List.of(
                ResponseItemDTO.builder().id(1).name("Дрель").build(),
                ResponseItemDTO.builder().id(2).name("saw").build()
        );

        Mockito.when(itemService.getItemsOfUser(10)).thenReturn(dtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("saw"));

        Mockito.verify(itemService).getItemsOfUser(10);
    }

    @Test
    @SneakyThrows
    void search() {
        List<ResponseItemDTO> dtoList = List.of(
                ResponseItemDTO.builder().id(1).name("Дрель").build()
        );

        Mockito.when(itemService.search("Дрель")).thenReturn(dtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", "Дрель"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));

        Mockito.verify(itemService).search("Дрель");
    }

    @Test
    @SneakyThrows
    void createComment() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("good item")
                .build();

        ResponseCommentDTO dtoResponse = ResponseCommentDTO.builder()
                .id(1)
                .text("good item")
                .authorName("Ann")
                .build();

        Mockito.when(itemService.createComment(any(CreateCommentDTO.class), eq(20), eq(1))).thenReturn(dtoResponse);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("good item"));

        ArgumentCaptor<CreateCommentDTO> captor = ArgumentCaptor.forClass(CreateCommentDTO.class);
        Mockito.verify(itemService).createComment(captor.capture(), eq(20), eq(1));

        Assertions.assertEquals("good item", captor.getValue().getText());
    }

    @Test
    @SneakyThrows
    void createComment_notFound_thenReturnNotFound() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("good item")
                .build();

        Mockito.when(itemService.createComment(any(CreateCommentDTO.class), eq(20), eq(1)))
                .thenThrow(new NotFoundException("user or item not found"));

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("user or item not found"));
    }
}