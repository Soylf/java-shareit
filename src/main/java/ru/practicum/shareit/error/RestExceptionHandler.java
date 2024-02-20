package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.error.exception.*;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final EntityNotFoundException e) {
        log.info("404 {}", e.getMessage());
        return new ApiError(e.getMessage(), getStackTraceAsString(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleBadRequestExceptionEx(final BadRequestException e) {
        log.info("400 {}", e.getMessage());
        return new ApiError(e.getMessage(), getStackTraceAsString(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiError handleThrowable(final Throwable e) {
        log.info("500 {}", e.getMessage());
        return new ApiError(e.getMessage(), getStackTraceAsString(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ApiError handleBadConflictException(final BadConflictException e) {
        log.info("409 {}", e.getMessage());
        return new ApiError(e.getMessage(), getStackTraceAsString(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleNoEnumValueArgumentException(final NoEnumValueArgumentException e) {
        log.error("Unknown state: UNSUPPORTED_STATUS, {}", e.getMessage());
        return new ApiError("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    private String getStackTraceAsString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}