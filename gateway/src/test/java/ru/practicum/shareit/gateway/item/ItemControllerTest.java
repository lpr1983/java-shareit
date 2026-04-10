package ru.practicum.shareit.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;
import ru.practicum.shareit.common.dto.ResponseItemDTO;
import ru.practicum.shareit.gateway.error.ErrorHandler;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@Import(ErrorHandler.class)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient itemClient;

    @Test
    @SneakyThrows
    void create_validData_thenCallsClient() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("Дрель")
                .description("Мощная")
                .available(true)
                .requestId(7)
                .build();

        ResponseEntity<Object> returnEntity = ResponseEntity.ok().build();
        Mockito.when(itemClient.create(any(CreateItemDTO.class), eq(1))).thenReturn(returnEntity);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<CreateItemDTO> captor = ArgumentCaptor.forClass(CreateItemDTO.class);
        verify(itemClient).create(captor.capture(), eq(1));

        CreateItemDTO actual = captor.getValue();
        assertEquals("Дрель", actual.getName());
        assertEquals("Мощная", actual.getDescription());
        assertEquals(true, actual.getAvailable());
        assertEquals(7, actual.getRequestId());
    }

    @Test
    @SneakyThrows
    void create_invalidData_thenReturnBadRequest() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле name")))
                .andExpect(jsonPath("$.error", containsString("Поле description")))
                .andExpect(jsonPath("$.error", containsString("Поле available")));

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void create_invalidOwnerId_thenReturnBadRequest() {
        CreateItemDTO dto = CreateItemDTO.builder()
                .name("drill")
                .description("powerful")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void update_validData_thenCallsClient() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("updated")
                .description("updated desc")
                .available(false)
                .build();

        ResponseEntity<Object> returnEntity = ResponseEntity.ok().build();
        Mockito.when(itemClient.update(any(UpdateItemDTO.class),
                eq(1), eq(2))).thenReturn(returnEntity);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<UpdateItemDTO> captor = ArgumentCaptor.forClass(UpdateItemDTO.class);
        verify(itemClient).update(captor.capture(), eq(1), eq(2));

        UpdateItemDTO actual = captor.getValue();
        assertEquals("updated", actual.getName());
        assertEquals("updated desc", actual.getDescription());
        assertEquals(false, actual.getAvailable());
    }

    @Test
    @SneakyThrows
    void update_nullableFields_thenCallsClient() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<UpdateItemDTO> captor = ArgumentCaptor.forClass(UpdateItemDTO.class);
        verify(itemClient).update(captor.capture(), eq(1), eq(2));

        UpdateItemDTO actual = captor.getValue();
        assertNull(actual.getName());
        assertNull(actual.getDescription());
        assertNull(actual.getAvailable());
    }

    @Test
    @SneakyThrows
    void update_invalidData_thenReturnBadRequest() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("")
                .description("")
                .build();

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле name")))
                .andExpect(jsonPath("$.error", containsString("Поле description")));

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void update_invalidItemId_thenReturnBadRequest() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("updated")
                .build();

        mockMvc.perform(patch("/items/0")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void update_invalidOwnerId_thenReturnBadRequest() {
        UpdateItemDTO dto = UpdateItemDTO.builder()
                .name("updated")
                .build();

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void getById_validIds_thenCallsClient() {
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 2))
                .andReturn();

        verify(itemClient).getById(1, 2);
    }

    @Test
    @SneakyThrows
    void getById_invalidItemId_thenReturnBadRequest() {
        mockMvc.perform(get("/items/0")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void getById_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void getItemsOfUser_returnDTO() {
        List<ResponseItemDTO> responseDTOList = List.of(
                ResponseItemDTO.builder().id(1).name("item").build()
        );
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(responseDTOList);
        Mockito.when(itemClient.getItemsOfUser(1)).thenReturn(responseEntity);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    @SneakyThrows
    void getItemsOfUser_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void search_validText_thenCallsClient() {
        mockMvc.perform(get("/items/search")
                        .param("text", "Дрель"))
                .andReturn();

        verify(itemClient).search("Дрель");
    }

    @Test
    @SneakyThrows
    void search_emptyText_thenCallsClient() {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andReturn();

        verify(itemClient).search("");
    }

    @Test
    @SneakyThrows
    void createComment_validData_thenCallsClient() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("good item")
                .build();

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<CreateCommentDTO> captor = ArgumentCaptor.forClass(CreateCommentDTO.class);
        verify(itemClient).createComment(captor.capture(), eq(3), eq(1));

        CreateCommentDTO actual = captor.getValue();
        assertEquals("good item", actual.getText());
    }

    @Test
    @SneakyThrows
    void createComment_invalidText_thenReturnBadRequest() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("")
                .build();

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле text")));

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void createComment_invalidUserId_thenReturnBadRequest() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("good item")
                .build();

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void createComment_invalidItemId_thenReturnBadRequest() {
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .text("good item")
                .build();

        mockMvc.perform(post("/items/0/comment")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }
}