package ru.practicum.shareit.common.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateBookingDTO {
    @NotNull
    private Integer itemId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
}
