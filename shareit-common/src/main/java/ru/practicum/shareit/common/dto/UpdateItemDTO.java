package ru.practicum.shareit.common.dto;

import lombok.Data;
import ru.practicum.shareit.common.annotation.NullOrNotBlank;

@Data
public class UpdateItemDTO {
    @NullOrNotBlank(message = "Имя не может быть пустым")
    private String name;
    @NullOrNotBlank(message = "Описание не может быть пустым")
    private String description;
    private Boolean available;
}
