package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private String name;

    @Email(message = "email в неправильном формате")
    private String email;
}
