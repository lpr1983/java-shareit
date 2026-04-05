package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.dto.CreateBookingDTO;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateBookingDTO createBookingDto,
                                         @RequestHeader("X-Sharer-User-Id") @Positive int userId) {
        return bookingClient.create(createBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") @Positive int userId,
                                          @PathVariable @Positive int bookingId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") @Positive int userId,
                                          @PathVariable @Positive int bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") @Positive int userId,
                                                    @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingClient.getBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") @Positive int userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingClient.getBookingsOfOwner(userId, state);
    }
}