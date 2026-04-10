package ru.practicum.shareit.common.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.common.annotation.NullOrNotBlank;

@Data
@Builder
public class UpdateUserDTO {
    @NullOrNotBlank
    private String name;

    @Email(message = "Email в неправильном формате")
    private String email;
}
