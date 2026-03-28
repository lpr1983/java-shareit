package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CreateCommentDTO;
import ru.practicum.shareit.item.dto.CreateItemDTO;
import ru.practicum.shareit.item.dto.ResponseCommentDTO;
import ru.practicum.shareit.item.dto.ResponseItemDTO;
import ru.practicum.shareit.item.dto.UpdateItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseItemDTO create(@Valid @RequestBody CreateItemDTO itemToCreate, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.create(itemToCreate, ownerId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseItemDTO update(@Valid @RequestBody UpdateItemDTO itemToUpdate,
                                  @PathVariable int itemId,
                                  @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.update(itemToUpdate, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDTO getById(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping
    public List<ResponseItemDTO> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public List<ResponseItemDTO> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDTO createComment(@Valid @RequestBody CreateCommentDTO dto,
                                            @RequestHeader("X-Sharer-User-Id") int userId,
                                            @PathVariable int itemId) {

        return itemService.createComment(dto, userId, itemId);
    }

}
