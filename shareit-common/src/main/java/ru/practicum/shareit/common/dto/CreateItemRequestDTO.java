package ru.practicum.shareit.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateItemRequestDTO {
    @NotBlank
    private String description;
}
