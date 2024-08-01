package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class User {
    @NotNull(groups = Marker.OnUpdate.class,
            message = "Не задан id пользователя")
    long id;
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Адрес почты не может быть пустым")
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Ошибка в формате email")
    String email;
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Логин не должен быть пустым")
    String login;
    String name;
    @Past(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Дата рождения должна быть в прошлом")
    LocalDate birthday;
}
