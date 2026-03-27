package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDTO {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank
    @Email(message = "Email в неправильном формате")
    private String email;
}
