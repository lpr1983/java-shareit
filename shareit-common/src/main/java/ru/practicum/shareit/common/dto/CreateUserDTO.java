package ru.practicum.shareit.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserDTO {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank
    @Email(message = "Email в неправильном формате")
    private String email;
}
