package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.ApiError;
import ru.practicum.shareit.error.exception.BadRequestException;

import java.util.Map;


@RestControllerAdvice("ru.practicum.shareit")
public class RestExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIsNotValidFieldException(final BadRequestException e) {
        return new ApiError(e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handlePostmanTest(final IllegalStateException e) {
        return Map.of("error", e.getMessage());
    }


}