package ru.practicum.shareit.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private String error;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public ApiError(String message, String error) {
        this.error = error;
        this.message = message;
    }

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, StackTraceElement[] stackTrace) {
        this.message = message;
    }
}