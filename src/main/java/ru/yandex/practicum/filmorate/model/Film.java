package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Film {
    @NotNull(groups = Marker.OnUpdate.class,
            message = "Не задан id фильма")
    long id;
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Название фильмане может быть пустым")
    String name;
    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            max = 200,
            message = "Описание не должно быть длинее 200 символов")
    String description;
    @ReleaseDate(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Дата выхода фильма должна быть не ранее 28.12.1985")
    LocalDate releaseDate;
    @Positive(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Продолжительность должна быть целым числом")
    int duration;
}
