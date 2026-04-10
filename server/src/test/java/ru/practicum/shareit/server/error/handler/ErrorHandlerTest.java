package ru.practicum.shareit.server.error.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.server.error.exception.ConflictException;
import ru.practicum.shareit.server.error.exception.NotFoundException;

class ErrorHandlerTest {

    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_thenReturnErrorResponse() {
        NotFoundException ex = new NotFoundException("Не найден пользователь с id: 1");

        ErrorResponse result = errorHandler.handleNotFound(ex);

        Assertions.assertEquals("Не найден пользователь с id: 1", result.getError());
    }

    @Test
    void handleConflict_thenReturnErrorResponse() {
        ConflictException ex = new ConflictException("Email уже используется");

        ErrorResponse result = errorHandler.handleConflictException(ex);

        Assertions.assertEquals("Email уже используется", result.getError());
    }

    @Test
    void handleValidation_thenReturnErrorResponse() {
        ValidationException ex = new ValidationException("Некорректные данные");

        ErrorResponse result = errorHandler.handleValidationException(ex);

        Assertions.assertEquals("Некорректные данные", result.getError());
    }

    @Test
    void handleInternalError_thenReturnErrorResponse() {
        Exception ex = new Exception("Что-то неизвестное");

        ErrorResponse result = errorHandler.handleInternalError(ex);

        Assertions.assertEquals("Internal server error", result.getError());
    }

}