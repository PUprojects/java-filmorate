package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    List<Film> getAll();

    Optional<Film> getById(long filmId);

    Film create(Film film);

    void update(Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getPopular(int count);
}
