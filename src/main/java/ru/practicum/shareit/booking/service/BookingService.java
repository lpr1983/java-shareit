package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDTO;
import ru.practicum.shareit.booking.dto.ResponseBookingDTO;

import java.util.List;

public interface BookingService {

    ResponseBookingDTO create(CreateBookingDTO createBookingDto, int userId);

    ResponseBookingDTO approve(int bookingId, int userId, boolean approved);

    ResponseBookingDTO getById(int userId, int bookingId);

    List<ResponseBookingDTO> getBookingsOfUser(int userId, String state);

    List<ResponseBookingDTO> getBookingsOfOwner(int userId, String state);
}
