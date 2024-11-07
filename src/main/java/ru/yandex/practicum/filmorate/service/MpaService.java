package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final JdbcMpaRepository mpaRepository;

    public Mpa get(long id) {
        return mpaRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Not found mpa ration with id = " + id));
    }

    public List<Mpa> getAll() {
        return mpaRepository.getAll();
    }
}
