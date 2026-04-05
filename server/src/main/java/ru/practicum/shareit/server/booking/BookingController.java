package ru.practicum.shareit.server.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.server.booking.dto.ResponseBookingDTO;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseBookingDTO create(@Valid @RequestBody CreateBookingDTO createBookingDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.create(createBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    ResponseBookingDTO approve(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId, @RequestParam boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseBookingDTO getById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    List<ResponseBookingDTO> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    List<ResponseBookingDTO> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfOwner(userId, state);
    }

}
