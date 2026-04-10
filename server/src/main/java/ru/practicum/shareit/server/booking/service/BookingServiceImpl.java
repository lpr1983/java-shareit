package ru.practicum.shareit.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.common.dto.ResponseBookingDTO;
import ru.practicum.shareit.common.model.BookingStateFilter;
import ru.practicum.shareit.common.model.BookingStatus;
import ru.practicum.shareit.server.booking.mappers.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.storage.BookingStorage;
import ru.practicum.shareit.server.error.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.storage.ItemStorage;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;

    @Override
    public List<ResponseBookingDTO> getBookingsOfOwner(int userId, String state) {
        log.info("getBookingsOfOwner, userId {}, state {}", userId, state);

        BookingStateFilter filter = BookingStateFilter.tryParse(state);

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
        log.info("getBookingsOfUser, userId {}, state {}", userId, state);

        BookingStateFilter filter = BookingStateFilter.tryParse(state);

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
        log.info("getById, userId {}, bookingId {}", userId, bookingId);

        Booking booking = checkBookingExistsAndReturnIt(bookingId);

        if (booking.getItem().getOwnerId() != userId && booking.getBooker().getId() != userId) {
            throw new ValidationException(String.format("Пользователь с id = %d не является не автором брони, ни владельцем вещи", userId));
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public ResponseBookingDTO approve(int bookingId, int userId, boolean approved) {
        log.info("approve, userId {}, bookingId {}, approved", userId, bookingId, approved);

        Booking booking = checkBookingExistsAndReturnIt(bookingId);

        Item item = booking.getItem();
        if (item.getOwnerId() != userId) {
            throw new ValidationException(String.format("Пользователь с id %d не является владельцем вещи с id %d",
                    userId, item.getId()));
        }

        BookingStatus currentStatus = booking.getStatus();

        if (currentStatus != BookingStatus.WAITING) {
            throw new ValidationException("Операция недоступна в статусе " + currentStatus);
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
        log.info("create, userId {}, dto {}", userId, createBookingDto);

        if (createBookingDto.getStart().isAfter(createBookingDto.getEnd())) {
            throw new ValidationException("Время завершения не может быть меньше времени начала");
        }

        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));

        int itemId = createBookingDto.getItemId();
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        if (!item.isAvailable()) {
            throw new ValidationException("Недоступна вещь с id: " + itemId);
        }

        if (userId == item.getOwnerId()) {
            throw new ValidationException("Нельзя бронировать собственную вещь");
        }

        Booking booking = bookingMapper.toEntity(createBookingDto, booker, item);
        Booking createdBooking = bookingStorage.save(booking);

        return bookingMapper.toResponseDto(createdBooking);
    }

    private Booking checkBookingExistsAndReturnIt(int bookingId) {
        return bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id: " + bookingId));
    }
}
