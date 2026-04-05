package ru.practicum.shareit.server.user.dto;

import lombok.Data;

@Data
public class ResponseUserDTO {
    private int id;
    private String name;
    private String email;
}
