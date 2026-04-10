package ru.practicum.shareit.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseCommentDTO {
    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
