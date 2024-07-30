package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(groups = Marker.OnUpdate.class,
            message = "Не задан id пользователя")
    private long id;
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Адрес почты не может быть пустым")
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Ошибка в формате email")
    private String email;
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @Past(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}
