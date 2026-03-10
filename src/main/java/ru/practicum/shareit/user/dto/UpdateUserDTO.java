package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.annotation.NullOrNotBlank;

@Data
public class UpdateUserDTO {
    @NullOrNotBlank
    private String name;

    @Email(message = "Email в неправильном формате")
    private String email;
}
