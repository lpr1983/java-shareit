package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ResponseItemDTO;
import ru.practicum.shareit.user.dto.ResponseUserDTO;

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
