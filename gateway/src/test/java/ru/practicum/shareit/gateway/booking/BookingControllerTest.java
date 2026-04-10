package ru.practicum.shareit.gateway.booking;

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
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.gateway.error.ErrorHandler;
import ru.practicum.shareit.common.dto.ResponseBookingDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
@Import(ErrorHandler.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Test
    @SneakyThrows
    void create_validData_thenCallsClient() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(start)
                .end(end)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ArgumentCaptor<CreateBookingDTO> captor = ArgumentCaptor.forClass(CreateBookingDTO.class);
        verify(bookingClient).create(captor.capture(), eq(1));

        CreateBookingDTO actual = captor.getValue();
        assertEquals(10, actual.getItemId());
        assertEquals(start, actual.getStart());
        assertEquals(end, actual.getEnd());
    }

    @Test
    @SneakyThrows
    void create_invalidData_thenReturnBadRequest() {
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле itemId")))
                .andExpect(jsonPath("$.error", containsString("Поле start")))
                .andExpect(jsonPath("$.error", containsString("Поле end")));

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void create_pastTime_thenReturnBadRequest() {
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusYears(1))
                .end(LocalDateTime.now().minusYears(1))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Поле start")))
                .andExpect(jsonPath("$.error", containsString("Поле end")));

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void create_invalidUserId_thenReturnBadRequest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(start)
                .end(end)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 0)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void approve_validData_thenCallsClient() {
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andReturn();

        verify(bookingClient).approve(2, 1, true);
    }

    @Test
    @SneakyThrows
    void approve_invalidBookingId_thenReturnBadRequest() {
        mockMvc.perform(patch("/bookings/0")
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void approve_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 0)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getById_validData_thenCallsClient() {
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 2))
                .andReturn();

        verify(bookingClient).getById(2, 1);
    }

    @Test
    @SneakyThrows
    void getById_invalidBookingId_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings/0")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getById_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", -1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser_returnDTO() {
        List<ResponseBookingDTO> responseDTOList = List.of(
                ResponseBookingDTO.builder().id(1).build()
        );
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(responseDTOList);
        Mockito.when(bookingClient.getBookingsOfUser(1, "ALL")).thenReturn(responseEntity);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser_validState_thenCallsClient() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "PAST"))
                .andReturn();

        verify(bookingClient).getBookingsOfUser(1, "PAST");
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser_defaultState_thenCallsClient() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andReturn();

        verify(bookingClient).getBookingsOfUser(1, "ALL");
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner_returnDTO() {
        List<ResponseBookingDTO> responseDTOList = List.of(
                ResponseBookingDTO.builder().id(1).build()
        );
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(responseDTOList);
        Mockito.when(bookingClient.getBookingsOfOwner(1, "ALL")).thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner_validState_thenCallsClient() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "FUTURE"))
                .andReturn();

        verify(bookingClient).getBookingsOfOwner(1, "FUTURE");
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner_defaultState_thenCallsClient() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andReturn();

        verify(bookingClient).getBookingsOfOwner(1, "ALL");
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner_invalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}