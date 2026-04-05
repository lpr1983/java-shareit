package ru.practicum.shareit.server.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.item.dto.ResponseItemDTO;
import ru.practicum.shareit.server.user.dto.ResponseUserDTO;
import ru.practicum.shareit.common.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseBookingDTO {
    private int id;
    private ResponseItemDTO item;
    private ResponseUserDTO booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
