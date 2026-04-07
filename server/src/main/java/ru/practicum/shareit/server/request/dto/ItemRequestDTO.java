package ru.practicum.shareit.server.request.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDTO {
    private int id;
    String description;
    LocalDateTime created;
    List<ItemElementDTO> items;
}
