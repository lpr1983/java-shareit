package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBookingDTO;
import ru.practicum.shareit.booking.dto.ResponseBookingDTO;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;

    @Override
    public List<ResponseBookingDTO> getBookingsOfOwner(int userId, String state) {
        BookingStateFilter filter = BookingStateFilter.parse(state);

        if (filter == null) {
            throw new ValidationException("Неизвестный фильтр state: " + state);
        }

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь с id: " + userId);
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (filter) {
            case ALL -> bookingStorage.findByItemOwnerId(userId);
            case REJECTED -> bookingStorage.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
            case WAITING -> bookingStorage.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case CURRENT -> bookingStorage.findCurrentByItemOwnerIdAndStatus(userId, BookingStatus.APPROVED, now);
            case FUTURE -> bookingStorage.findFutureByItemOwnerIdAndStatus(userId, BookingStatus.APPROVED, now);
            case PAST -> bookingStorage.findPastByItemOwnerIdAndStatus(userId, BookingStatus.APPROVED, now);
        };

        return bookings.stream().map(bookingMapper::toResponseDto).toList();
    }

    @Override
    public List<ResponseBookingDTO> getBookingsOfUser(int userId, String state) {
        BookingStateFilter filter = BookingStateFilter.parse(state);

        if (filter == null) {
            throw new ValidationException("Неизвестный фильтр state: " + state);
        }

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь с id: " + userId);
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (filter) {
            case ALL -> bookingStorage.findByBookerId(userId);
            case REJECTED -> bookingStorage.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
            case WAITING -> bookingStorage.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case CURRENT -> bookingStorage.findCurrentByBookerIdAndStatus(userId, BookingStatus.APPROVED, now);
            case FUTURE -> bookingStorage.findFutureByBookerIdAndStatus(userId, BookingStatus.APPROVED, now);
            case PAST -> bookingStorage.findPastByBookerIdAndStatus(userId, BookingStatus.APPROVED, now);
        };

        return bookings.stream().map(bookingMapper::toResponseDto).toList();
    }

    @Override
    public ResponseBookingDTO getById(int userId, int bookingId) {

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id: " + bookingId));

        if (booking.getItem().getOwnerId() != userId && booking.getBooker().getId() != userId) {
            throw new ValidationException(String.format("Пользователь с id = %n не является не автором брони, ни владельцем вещи", userId));
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public ResponseBookingDTO approve(int bookingId, int userId, boolean approved) {

        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id: " + bookingId));

        Item item = booking.getItem();
        if (item.getOwnerId() != userId) {
            throw new ValidationException(String.format("Пользователь с id %n не является владельцем вещи с id %n", userId, item.getId()));
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking savedBooking = bookingStorage.save(booking);

        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    public ResponseBookingDTO create(CreateBookingDTO createBookingDto, int userId) {

        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));

        int itemId = createBookingDto.getItemId();
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        if (!item.isAvailable()) {
            throw new ValidationException("Недоступна вещь с id: " + itemId);
        }

        Booking booking = bookingMapper.toEntity(createBookingDto, booker, item);
        Booking createdBooking = bookingStorage.save(booking);

        return bookingMapper.toResponseDto(createdBooking);
    }
}
