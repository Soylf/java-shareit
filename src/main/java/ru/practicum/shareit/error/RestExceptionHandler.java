package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.error.exception.ApiError;
import ru.practicum.shareit.error.exception.BadRequestException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;


import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());

        logger.debug("Объект не найден.");
        ApiError error = new ApiError("Объект не найден", ex.getMessage(), errors);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequestExceptionEx(BadRequestException ex, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());

        logger.debug("Неверный запрос.");
        ApiError error = new ApiError("Неверный запрос", ex.getMessage(), errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({Throwable.class})
    protected ResponseEntity<Object> handleThrowable(Throwable ex, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());

        logger.debug("Произошла внутренняя ошибка сервера.");
        ApiError error = new ApiError("Внутренняя ошибка сервера", ex.getMessage(), errors);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders header, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        logger.debug("Данные не прошли валидацию.");
        ApiError apiError = new ApiError("Некорректные данные", ex.getMessage(), errors);
        return new ResponseEntity<>(apiError, status);
    }

}