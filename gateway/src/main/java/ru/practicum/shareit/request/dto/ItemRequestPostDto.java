package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestPostDto {
    @NotBlank
    private String description;
}