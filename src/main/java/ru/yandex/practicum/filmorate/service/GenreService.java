package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final JdbcGenreRepository jdbcGenreRepository;

    public List<Genre> getAll() {
        return jdbcGenreRepository.getAll();
    }

    public Genre get(long genreId) {
        return jdbcGenreRepository.getById(genreId)
                .orElseThrow(() -> new NotFoundException("Not found genre with id = " + genreId));
    }
}
