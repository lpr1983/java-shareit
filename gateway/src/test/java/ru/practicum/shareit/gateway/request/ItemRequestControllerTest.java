package ru.practicum.shareit.gateway.request;

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
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.gateway.error.ErrorHandler;
import ru.practicum.shareit.common.dto.ItemRequestDTO;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    ItemRequestClient itemRequestClient;

    @Test
    @SneakyThrows
    void create_validData_thenCallsClient() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("Нужна дрель")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<CreateItemRequestDTO> captor = ArgumentCaptor.forClass(CreateItemRequestDTO.class);
        verify(itemRequestClient).create(captor.capture(), eq(1));

        CreateItemRequestDTO actual = captor.getValue();
        assertEquals("Нужна дрель", actual.getDescription());
    }

    @Test
    @SneakyThrows
    void create_invalidData_thenReturnBadRequest() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле description")));

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    @SneakyThrows
    void create_invalidUserId_thenReturnBadRequest() {
        CreateItemRequestDTO dto = CreateItemRequestDTO.builder()
                .description("Нужна дрель")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    @SneakyThrows
    void getMyRequests_returnDTO() {
        List<ItemRequestDTO> responseDTOList = List.of(
                ItemRequestDTO.builder().id(1).description("Нужна дрель").build()
        );
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(responseDTOList);
        Mockito.when(itemRequestClient.getMyRequests(1)).thenReturn(responseEntity);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    @SneakyThrows
    void getMyRequests_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    @SneakyThrows
    void getAll_validParams_thenCallsClient() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andReturn();

        verify(itemRequestClient).getAllRequestsExceptMy(1, 0, 10);
    }

    @Test
    @SneakyThrows
    void getAll_defaultParams_thenCallsClient() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andReturn();

        verify(itemRequestClient).getAllRequestsExceptMy(1, 0, 50);
    }

    @Test
    @SneakyThrows
    void getAll_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 0)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    @SneakyThrows
    void getAll_invalidFrom_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    @SneakyThrows
    void getAll_invalidSize_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

    @Test
    @SneakyThrows
    void getById_validIds_thenCallsClient() {
        mockMvc.perform(get("/requests/1"))
                .andReturn();

        verify(itemRequestClient).getById(1);
    }

    @Test
    @SneakyThrows
    void getById_invalidRequestId_thenReturnBadRequest() {
        mockMvc.perform(get("/requests/0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }

}