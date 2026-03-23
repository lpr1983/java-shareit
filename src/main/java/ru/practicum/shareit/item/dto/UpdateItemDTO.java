package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.annotation.NullOrNotBlank;

@Data
public class UpdateItemDTO {
    @NullOrNotBlank(message = "Имя не может быть пустым")
    private String name;
    @NullOrNotBlank(message = "Описание не может быть пустым")
    private String description;
    private Boolean available;
}
