package ru.practicum.shareit.booking.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.dto.CreateBookingDTO;
import ru.practicum.shareit.booking.dto.ResponseBookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingMapper {

    @Mapping(target = "item.lastBooking", ignore = true)
    @Mapping(target = "item.nextBooking", ignore = true)
    @Mapping(target = "item.comments", ignore = true)
    ResponseBookingDTO toResponseDto(Booking booking);

    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "id", ignore = true)
    Booking toEntity(CreateBookingDTO dto, User booker, Item item);
}