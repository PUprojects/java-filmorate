package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getAll() {
        log.info("Get all films start");
        List<Film> result = filmService.getAll();
        log.info("Get all films end");
        return result;
    }

    @GetMapping("/{filmId}")
    public Film get(@PathVariable long filmId) {
        log.info("Get film {} start", filmId);
        return filmService.get(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.OnCreate.class})
    public Film create(@RequestBody @Valid Film newFilm) {
        log.info("Post new film {} start", newFilm);
        filmService.create(newFilm);
        log.info("Post new film {} end", newFilm);
        return newFilm;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film update(@RequestBody @Valid Film film) {
        log.info("Update film {} start", film);
        filmService.update(film);
        log.info("Update film {} end", film);
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Add like to film {} from user {} start", filmId, userId);
        filmService.addLike(filmId, userId);
        log.info("Add like to film {} from user {} end", filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Delete like to film {} from user {} start", filmId, userId);
        filmService.deleteLike(filmId, userId);
        log.info("Delete like to film {} from user {} end", filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(value = "count", defaultValue = "10") int count) {
        log.info("Get {} popular films start", count);
        List<Film> result = filmService.getPopular(count);
        log.info("Get {} popular films end", count);
        return result;
    }
}
