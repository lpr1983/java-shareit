package ru.practicum.shareit.booking.model;

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
            return null;
        }
    }
}
