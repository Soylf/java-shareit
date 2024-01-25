package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NonNull
public class UserDto {
    private Long id;
    private String name;
    private String email;
}