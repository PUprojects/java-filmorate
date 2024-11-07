package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Get all mpa start");
        List<Mpa> result = mpaService.getAll();
        log.info("Get all map end");
        return result;
    }

    @GetMapping("/{mpaId}")
    public Mpa getById(@PathVariable long mpaId) {
        log.info("Get mpa by id = {} start", mpaId);
        Mpa result = mpaService.get(mpaId);
        log.info("Get map by id = {} end", mpaId);
        return result;
    }
}
