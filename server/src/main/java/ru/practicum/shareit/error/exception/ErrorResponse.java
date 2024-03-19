package ru.practicum.shareit.error.exception;

public class ErrorResponse {
    private String message;
    private String stackTrace;

    public ErrorResponse(String message, String stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}