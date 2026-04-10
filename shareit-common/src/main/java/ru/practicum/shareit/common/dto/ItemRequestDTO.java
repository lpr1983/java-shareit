package ru.practicum.shareit.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemRequestDTO {
    private int id;
    String description;
    LocalDateTime created;
    List<ItemElementDTO> items;
}
