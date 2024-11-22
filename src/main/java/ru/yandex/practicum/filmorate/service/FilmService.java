package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final JdbcFilmRepository filmRepository;
    private final JdbcUserRepository userRepository;
    private final JdbcGenreRepository genreRepository;
    private final JdbcMpaRepository mpaRepository;

    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    public Film get(long filmId) {
        return filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Not found fim with id = " + filmId));
    }

    public Film create(Film film) {
        film.setLikesCount(0);
        Mpa mpa = mpaRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new BadRequestException("Not found MPA rating with id " + film.getMpa().getId()));
        film.setMpa(mpa);
        if (film.getGenres() != null) {
            List<Genre> genres = genreRepository.getByIds(film.getGenres().stream()
                    .map(Genre::getId)
                    .toList());
            if (genres.size() != film.getGenres().size()) {
                genres.forEach(film.getGenres()::remove);
                throw new BadRequestException("Some genres not found: " +
                        film.getGenres().stream()
                                .map(Genre::getId)
                                .toList());
            }
            film.setGenres(new LinkedHashSet<>(genres));
        }

        return filmRepository.create(film);
    }

    public Film update(Film film) {
        Film savedFilm = get(film.getId());
        film.setLikesCount(savedFilm.getLikesCount());
        mpaRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Not found MPA rating with id " + film.getMpa().getId()));
        if (film.getGenres() == null) {
            film.setGenres(savedFilm.getGenres());
        } else {
            List<Genre> genres = genreRepository.getByIds(film.getGenres().stream()
                    .map(Genre::getId)
                    .toList());
            if (genres.size() != film.getGenres().size()) {
                genres.forEach(film.getGenres()::remove);
                throw new NotFoundException("Some genres not found: " +
                        film.getGenres().stream()
                                .map(Genre::getId)
                                .toList());
            }
        }
        filmRepository.update(film);
        return film;
    }

    public void addLike(long filmId, long userId) {
        Film film = get(filmId);
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
        filmRepository.addLike(filmId, userId);
        film.setLikesCount(film.getLikesCount() + 1);
    }

    public void deleteLike(long filmId, long userId) {
        Film film = get(filmId);
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));

        filmRepository.deleteLike(filmId, userId);
        film.setLikesCount(film.getLikesCount() - 1);
    }

    public List<Film> getPopular(int count) {
        return filmRepository.getPopular(count);
    }
}
