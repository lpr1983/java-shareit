package ru.practicum.shareit.server.booking.model;

import java.time.LocalDateTime;

public interface BookingDatesOfItem {
    int getItemId();

    LocalDateTime getLastStart();

    LocalDateTime getNextStart();
}