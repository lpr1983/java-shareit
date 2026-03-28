package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.error.exception.ValidationException;

public enum BookingStateFilter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingStateFilter parse(String str) {
        try {
            return BookingStateFilter.valueOf(str.trim().toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Неизвестный фильтр state: " + str);
        }
    }
}
