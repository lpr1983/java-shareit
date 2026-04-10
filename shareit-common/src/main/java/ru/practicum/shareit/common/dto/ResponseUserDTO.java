package ru.practicum.shareit.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseUserDTO {
    private int id;
    private String name;
    private String email;
}
