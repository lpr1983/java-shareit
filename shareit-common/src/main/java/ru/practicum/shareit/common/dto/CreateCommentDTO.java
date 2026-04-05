package ru.practicum.shareit.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDTO {
    @NotBlank
    private String text;
}
