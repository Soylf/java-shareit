package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NonNull
public class UserDto {
    private Long id;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @Email(message = "Некорректный email")
    @NotEmpty(message = "Email не может быть пустым")
    private String email;
}