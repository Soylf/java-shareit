package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NonNull
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;


    @NotEmpty(message = "Почта не должна быть пустой")
    private String email;
}