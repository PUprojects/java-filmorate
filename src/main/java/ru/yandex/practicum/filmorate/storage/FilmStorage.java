package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> get(long filmId);

    Film create(Film film);

    void update(Film film);

    int addLike(long filmId, long userId);

    int deleteLike(long filmId, long userId);

    List<Film> getPopular(int count);
}
