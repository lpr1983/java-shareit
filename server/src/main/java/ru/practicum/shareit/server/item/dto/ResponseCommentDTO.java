package ru.practicum.shareit.server.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseCommentDTO {
    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
