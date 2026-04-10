package ru.practicum.shareit.server.user;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.common.dto.CreateUserDTO;
import ru.practicum.shareit.common.dto.ResponseUserDTO;
import ru.practicum.shareit.common.dto.UpdateUserDTO;
import ru.practicum.shareit.server.error.exception.ConflictException;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.error.handler.ErrorHandler;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    UserService userService;

    @Test
    @SneakyThrows
    void getAll() {
        List<ResponseUserDTO> responseDTOlist = List.of(
                ResponseUserDTO.builder().id(1).build()
        );

        Mockito.when(userService.getAll()).thenReturn(responseDTOlist);

        mockMvc.perform(get("/users"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getUser_success_thenReturnOk() {
        int id = 1;
        ResponseUserDTO dto = ResponseUserDTO.builder().id(id).build();
        Mockito.when(userService.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(status().isOk());

        Mockito.verify(userService).getById(id);
    }

    @Test
    @SneakyThrows
    void getUser_notFound_thenReturnNotFound() {
        Mockito.when(userService.getById(1)).thenThrow(new NotFoundException(""));

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void create() {
        CreateUserDTO dto = CreateUserDTO.builder().name("test").email("test@test.test").build();
        ResponseUserDTO dtoResponse = ResponseUserDTO.builder().id(1).build();
        Mockito.when(userService.create(any())).thenReturn(dtoResponse);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        Mockito.verify(userService).create(any(CreateUserDTO.class));
    }

    @Test
    @SneakyThrows
    void delete() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 1))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(1);
    }

    @Test
    @SneakyThrows
    void update() {
        UpdateUserDTO dto = UpdateUserDTO.builder().name("test").build();

        mockMvc.perform(patch("/users/{id}", 1)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        ArgumentCaptor<UpdateUserDTO> captor = ArgumentCaptor.forClass(UpdateUserDTO.class);
        Mockito.verify(userService).update(eq(1), captor.capture());

        Assertions.assertEquals("test", captor.getValue().getName());
    }

    @Test
    @SneakyThrows
    void update_conflictEmail_thenReturnConflict() {
        UpdateUserDTO dto = UpdateUserDTO.builder().name("test").build();
        Mockito.when(userService.update(anyInt(), any(UpdateUserDTO.class)))
                .thenThrow(new ConflictException("conflict"));

        mockMvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("conflict"));

        Mockito.verify(userService).update(eq(1), any(UpdateUserDTO.class));
    }
}