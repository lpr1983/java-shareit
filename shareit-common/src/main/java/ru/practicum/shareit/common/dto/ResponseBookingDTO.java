package ru.practicum.shareit.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.common.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResponseBookingDTO {
    private int id;
    private ResponseItemDTO item;
    private ResponseUserDTO booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
