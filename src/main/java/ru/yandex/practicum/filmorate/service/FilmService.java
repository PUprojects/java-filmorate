package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film get(long filmId) {
        return filmStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Not found fim with id = " + filmId));
    }

    public Film create(Film film) {
        film.setLikesCount(0);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        Film savedFilm = get(film.getId());
        film.setLikesCount(savedFilm.getLikesCount());
        filmStorage.update(film);

        return film;
    }

    public void addLike(long filmId, long userId) {
        Film film = get(filmId);
        userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));

        film.setLikesCount(filmStorage.addLike(filmId, userId));
    }

    public void deleteLike(long filmId, long userId) {
        Film film = get(filmId);
        userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));

        film.setLikesCount(filmStorage.deleteLike(filmId, userId));
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}
