package ru.practicum.shareit.server.booking;

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
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.common.dto.ResponseBookingDTO;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.server.error.handler.ErrorHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    BookingService bookingService;

    @Test
    @SneakyThrows
    void create() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(start)
                .end(end)
                .build();

        ResponseBookingDTO dtoResponse = ResponseBookingDTO.builder()
                .id(1)
                .build();

        Mockito.when(bookingService.create(any(CreateBookingDTO.class), eq(5)))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 5)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        ArgumentCaptor<CreateBookingDTO> captor = ArgumentCaptor.forClass(CreateBookingDTO.class);
        Mockito.verify(bookingService).create(captor.capture(), eq(5));

        Assertions.assertEquals(10, captor.getValue().getItemId());
        Assertions.assertEquals(start, captor.getValue().getStart());
        Assertions.assertEquals(end, captor.getValue().getEnd());
    }

    @Test
    @SneakyThrows
    void create_notFound_thenReturnNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(start)
                .end(end)
                .build();

        Mockito.when(bookingService.create(any(CreateBookingDTO.class), eq(5)))
                .thenThrow(new NotFoundException("Не найдена вещь с id: 10"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 5)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найдена вещь с id: 10"));
    }

    @Test
    @SneakyThrows
    void create_validation_thenReturnBadRequest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        CreateBookingDTO dto = CreateBookingDTO.builder()
                .itemId(10)
                .start(end)
                .end(start)
                .build();

        Mockito.when(bookingService.create(any(CreateBookingDTO.class), eq(5)))
                .thenThrow(new ValidationException("Время завершения не может быть меньше времени начала"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 5)
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Время завершения не может быть меньше времени начала"));
    }

    @Test
    @SneakyThrows
    void approve() {
        ResponseBookingDTO dtoResponse = ResponseBookingDTO.builder()
                .id(1)
                .build();

        Mockito.when(bookingService.approve(1, 5, true))
                .thenReturn(dtoResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 5)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        Mockito.verify(bookingService).approve(1, 5, true);
    }

    @Test
    @SneakyThrows
    void approve_notFound_thenReturnNotFound() {
        Mockito.when(bookingService.approve(1, 5, true))
                .thenThrow(new NotFoundException("Не найдено бронирование с id: 1"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 5)
                        .param("approved", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найдено бронирование с id: 1"));
    }

    @Test
    @SneakyThrows
    void approve_validation_thenReturnBadRequest() {
        Mockito.when(bookingService.approve(1, 5, true))
                .thenThrow(new ValidationException("Операция недоступна в статусе APPROVED"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 5)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Операция недоступна в статусе APPROVED"));
    }

    @Test
    @SneakyThrows
    void getById_success_thenReturnOk() {
        ResponseBookingDTO dtoResponse = ResponseBookingDTO.builder()
                .id(1)
                .build();

        Mockito.when(bookingService.getById(5, 1))
                .thenReturn(dtoResponse);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        Mockito.verify(bookingService).getById(5, 1);
    }

    @Test
    @SneakyThrows
    void getById_notFound_thenReturnNotFound() {
        Mockito.when(bookingService.getById(5, 1))
                .thenThrow(new NotFoundException("Не найдено бронирование с id: 1"));

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найдено бронирование с id: 1"));
    }

    @Test
    @SneakyThrows
    void getById_validation_thenReturnBadRequest() {
        Mockito.when(bookingService.getById(5, 1))
                .thenThrow(new ValidationException("Пользователь с id = 5 не является не автором брони, ни владельцем вещи"));

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Пользователь с id = 5 не является не автором брони, ни владельцем вещи"));
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser() {
        List<ResponseBookingDTO> dtoList = List.of(
                ResponseBookingDTO.builder().id(1).build(),
                ResponseBookingDTO.builder().id(2).build()
        );

        Mockito.when(bookingService.getBookingsOfUser(5, "ALL"))
                .thenReturn(dtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        Mockito.verify(bookingService).getBookingsOfUser(5, "ALL");
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser_withState() {
        List<ResponseBookingDTO> dtoList = List.of(
                ResponseBookingDTO.builder().id(1).build()
        );

        Mockito.when(bookingService.getBookingsOfUser(5, "CURRENT"))
                .thenReturn(dtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 5)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        Mockito.verify(bookingService).getBookingsOfUser(5, "CURRENT");
    }

    @Test
    @SneakyThrows
    void getBookingsOfUser_notFound_thenReturnNotFound() {
        Mockito.when(bookingService.getBookingsOfUser(5, "ALL"))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 5"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 5))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с id: 5"));
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner() {
        List<ResponseBookingDTO> dtoList = List.of(
                ResponseBookingDTO.builder().id(1).build(),
                ResponseBookingDTO.builder().id(2).build()
        );

        Mockito.when(bookingService.getBookingsOfOwner(7, "ALL"))
                .thenReturn(dtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 7))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        Mockito.verify(bookingService).getBookingsOfOwner(7, "ALL");
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner_withState() {
        List<ResponseBookingDTO> dtoList = List.of(
                ResponseBookingDTO.builder().id(3).build()
        );

        Mockito.when(bookingService.getBookingsOfOwner(7, "FUTURE"))
                .thenReturn(dtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 7)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));

        Mockito.verify(bookingService).getBookingsOfOwner(7, "FUTURE");
    }

    @Test
    @SneakyThrows
    void getBookingsOfOwner_notFound_thenReturnNotFound() {
        Mockito.when(bookingService.getBookingsOfOwner(7, "ALL"))
                .thenThrow(new NotFoundException("Не найден пользователь с id: 7"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 7))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Не найден пользователь с id: 7"));
    }
}