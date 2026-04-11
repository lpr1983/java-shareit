package ru.practicum.shareit.gateway.user;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.ResponseUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.gateway.error.ErrorHandler;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class})
@Import(ErrorHandler.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Test
    @SneakyThrows
    void getAll_returnDTO() {
        List<ResponseUserDTO> responseDTOlist = List.of(
                ResponseUserDTO.builder().id(1).build()
        );
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(responseDTOlist);
        Mockito.when(userClient.getAll()).thenReturn(responseEntity);

        mockMvc.perform(get("/users"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @SneakyThrows
    void getUser_validId_thenCallsClient() {
        mockMvc.perform(get("/users/1"))
                .andReturn();

        verify(userClient).getById(1);
    }

    @Test
    @SneakyThrows
    void getUser_invalidId_thenReturnBadRequest() {
        mockMvc.perform(get("/users/-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    @SneakyThrows
    void create_validData_thenCallsClient() {
        CreateUserDTO createDTO = CreateUserDTO.builder().name("test").email("test@test.ru").build();
        ResponseEntity<Object> returnEntity = ResponseEntity.ok().build();
        Mockito.when(userClient.create(any(CreateUserDTO.class))).thenReturn(returnEntity);

        mockMvc.perform(post("/users").content(mapper.writeValueAsString(createDTO))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        ArgumentCaptor<CreateUserDTO> captor = ArgumentCaptor.forClass(CreateUserDTO.class);
        verify(userClient).create(captor.capture());

        CreateUserDTO actual = captor.getValue();
        assertEquals("test", actual.getName());
        assertEquals("test@test.ru", actual.getEmail());
    }

    @Test
    @SneakyThrows
    void create_invalidData_thenReturnBadRequest() {
        CreateUserDTO dto = CreateUserDTO.builder().name("").email("234").build();

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле name")))
                .andExpect(jsonPath("$.error", containsString("Поле email")));

        verifyNoInteractions(userClient);
    }

    @Test
    @SneakyThrows
    void delete_validId_thenCallsClient() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andReturn();

        verify(userClient).delete(1);
    }

    @Test
    @SneakyThrows
    void delete_invalidId_thenReturnBadRequest() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    @SneakyThrows
    void update_validData_thenCallsClient() {
        UpdateUserDTO dto = UpdateUserDTO.builder().name("test").email("test@test.ru").build();

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<UpdateUserDTO> captor = ArgumentCaptor.forClass(UpdateUserDTO.class);

        verify(userClient).update(eq(1), captor.capture());

        UpdateUserDTO actual = captor.getValue();
        assertEquals("test", actual.getName());
        assertEquals("test@test.ru", actual.getEmail());
    }

    @Test
    @SneakyThrows
    void update_invalidNameAndEmail_thenReturnBadRequest() {
        UpdateUserDTO dto = UpdateUserDTO.builder().name("").email("234").build();

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле name")))
                .andExpect(jsonPath("$.error", containsString("Поле email")));

        verifyNoInteractions(userClient);
    }

    @Test
    @SneakyThrows
    void update_nullableFields_thenCallsClient() {
        UpdateUserDTO dto = UpdateUserDTO.builder().name(null).email(null).build();

        mockMvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        ArgumentCaptor<UpdateUserDTO> captor = ArgumentCaptor.forClass(UpdateUserDTO.class);
        verify(userClient).update(eq(1), captor.capture());

        UpdateUserDTO actual = captor.getValue();
        assertNull(actual.getName());
        assertNull(actual.getEmail());
    }
}