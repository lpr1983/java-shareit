package ru.practicum.shareit.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateItemDTO {
    @NotBlank(message = "Наименование не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Поле available должно быть установлено")
    private Boolean available;

    private Integer requestId;
}
