package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ResponseCommentDTO {
    private int id;
    private String text;
    private String authorName;
    private Instant created;
}
