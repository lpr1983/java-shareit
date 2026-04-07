package ru.practicum.shareit.gateway.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;

@RestController
@RequestMapping("/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateItemRequestDTO dto,
            @RequestHeader("X-Sharer-User-Id") @Positive int userId
    ) {
        return itemRequestClient.create(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive int userId
    ) {
        return itemRequestClient.getMyRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @PathVariable @Positive int requestId
    ) {
        return itemRequestClient.getById(requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsExceptMy(
            @RequestHeader("X-Sharer-User-Id") @Positive int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "50") @Positive int size
    ) {
        return itemRequestClient.getAllRequestsExceptMy(userId, from, size);
    }
}