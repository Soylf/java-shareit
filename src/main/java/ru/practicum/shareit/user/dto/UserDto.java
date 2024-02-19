package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NonNull
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;

    @Email(groups = {Create.class, Update.class}, regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @NotEmpty(groups = Create.class, message = "Почта не должна быть пустой")
    private String email;
}