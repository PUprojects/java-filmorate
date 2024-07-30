package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();
    private long newFilmId = 0;

    private long getNewFilmId() {
        return ++newFilmId;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Get all films start");
        List<Film> result = new ArrayList<>(films.values());
        log.info("Get all films end");
        return result;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Film create(@RequestBody @Valid Film newFilm) {
        log.info("Post new film {} start", newFilm);
        newFilm.setId(getNewFilmId());
        films.put(newFilm.getId(), newFilm);
        log.info("Post new film {} end", newFilm);
        return newFilm;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film update(@RequestBody @Valid Film film) {
        log.info("Update film {} start", film);
        Film savedFilm = films.get(film.getId());
        if(savedFilm == null) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Update film {} end", film);
        return film;
    }
}
