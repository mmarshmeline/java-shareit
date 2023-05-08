package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {

    @Pattern(regexp = "^\\w+.*\\S$", message = "Неккоректное имя пользователя.")
    @Size(max = 255)
    private String name;

    @Email(message = "Некорректный email пользователя.")
    @NotNull(message = "Поле email обязательно к заполнению.")
    private String email;
}