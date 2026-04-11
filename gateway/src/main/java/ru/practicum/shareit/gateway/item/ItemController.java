package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.dto.CreateCommentDTO;
import ru.practicum.shareit.common.dto.CreateItemDTO;
import ru.practicum.shareit.common.dto.UpdateItemDTO;

import java.util.Collections;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateItemDTO itemToCreate,
                                         @RequestHeader("X-Sharer-User-Id") @Positive int ownerId) {
        return itemClient.create(itemToCreate, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateItemDTO itemToUpdate,
                                         @PathVariable @Positive int itemId,
                                         @RequestHeader("X-Sharer-User-Id") @Positive int ownerId) {
        return itemClient.update(itemToUpdate, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive int itemId,
                                          @RequestHeader("X-Sharer-User-Id") @Positive int ownerId) {
        return itemClient.getById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") @Positive int ownerId) {
        return itemClient.getItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CreateCommentDTO dto,
                                                @RequestHeader("X-Sharer-User-Id") @Positive int userId,
                                                @PathVariable @Positive int itemId) {
        return itemClient.createComment(dto, userId, itemId);
    }
}