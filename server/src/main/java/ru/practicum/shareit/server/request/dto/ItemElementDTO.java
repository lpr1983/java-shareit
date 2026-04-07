package ru.practicum.shareit.server.request.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemElementDTO {
    private int id;
    private String name;
    private int ownerId;
}
