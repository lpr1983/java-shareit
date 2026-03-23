package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ResponseItemDTO {
    private int id;
    private String name;
    private String description;
    private boolean available;
}
