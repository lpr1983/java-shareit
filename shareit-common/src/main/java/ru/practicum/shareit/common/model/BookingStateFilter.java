package ru.practicum.shareit.common.model;

import ru.practicum.shareit.common.exception.ValidationException;

public enum BookingStateFilter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingStateFilter tryParse(String str) {
        try {
            return BookingStateFilter.valueOf(str.trim().toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Неизвестный фильтр state: " + str);
        }
    }
}
