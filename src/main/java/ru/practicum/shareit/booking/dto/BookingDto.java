package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    @NotNull(message = "Поле start обязательно к заполнению.")
    @Future(message = "Начало бронирования не может быть в прошедшем времени.")
    private LocalDateTime start;

    @Future(message = "Конец бронирования не может быть в прошедшем времени.")
    @NotNull(message = "Поле end обязательно к заполнению.")
    private LocalDateTime end;

    @NotNull(message = "Поле itemId обязательно к заполнению.")
    @Min(value = 1, message = "Некорректный itemId у товара.")
    private Long itemId;
    private final Status status = Status.WAITING;
}