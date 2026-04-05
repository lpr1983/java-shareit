package ru.practicum.shareit.gateway.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.dto.CreateBookingDTO;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.common.model.BookingStateFilter;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Slf4j
@Component
public class BookingClient extends BaseClient {
    private static final String BASE_PATH = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BASE_PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(CreateBookingDTO createBookingDto, int userId) {
        log.debug("POST /bookings userId={} body={}", userId, createBookingDto);

        if (createBookingDto.getStart().isAfter(createBookingDto.getEnd())) {
            throw new ValidationException("Время завершения не может быть меньше времени начала");
        }

        return post("", userId, createBookingDto);
    }

    public ResponseEntity<Object> approve(int userId, int bookingId, boolean approved) {
        log.debug("PATCH /bookings/{} userId={} approved={}", bookingId, userId, approved);
        return patch("/" + bookingId + "?approved={approved}", userId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getById(int userId, int bookingId) {
        log.debug("GET /bookings/{} userId={}", bookingId, userId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsOfUser(int userId, String state) {
        log.debug("GET /bookings userId={} state={}", userId, state);
        BookingStateFilter.tryParse(state);
        return get("?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> getBookingsOfOwner(int userId, String state) {
        log.debug("GET /bookings/owner userId={} state={}", userId, state);
        BookingStateFilter.tryParse(state);
        return get("/owner?state={state}", userId, Map.of("state", state));
    }
}