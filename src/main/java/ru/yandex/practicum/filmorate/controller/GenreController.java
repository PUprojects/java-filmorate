package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAll() {
        log.info("Get all genres start");
        List<Genre> result = genreService.getAll();
        log.info("Get all genres start");
        return result;
    }

    @GetMapping("/{genreId}")
    public Genre getById(@PathVariable long genreId) {
        log.info("Get genre by id = {} start", genreId);
        Genre result = genreService.get(genreId);
        log.info("Get genre by id = {} start", genreId);
        return result;
    }
}
