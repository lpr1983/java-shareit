package ru.practicum.shareit.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResponseItemDTO {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<ResponseCommentDTO> comments;
}
