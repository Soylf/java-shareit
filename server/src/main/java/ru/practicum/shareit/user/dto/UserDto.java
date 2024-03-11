package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotEmpty(message = "Почта не должна быть пустой")
    private String email;
}