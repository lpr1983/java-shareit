package ru.practicum.shareit.server.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.dto.CreateItemRequestDTO;
import ru.practicum.shareit.common.dto.ItemRequestDTO;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDTO create(@Valid @RequestBody CreateItemRequestDTO dto, @Positive @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDTO> getMyRequests(@Positive @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getMyRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDTO getById(@PathVariable @Positive int requestId) {
        return itemRequestService.getById(requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDTO> getAllRequestsExceptMy(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "50") Integer size) {

        return itemRequestService.getAllRequestsExceptMy(userId, from, size);
    }

}
