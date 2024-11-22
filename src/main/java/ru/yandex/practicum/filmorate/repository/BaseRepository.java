package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.RepositoryException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final NamedParameterJdbcOperations jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, SqlParameterSource parameters) {
        try (Stream<T> stream = jdbc.queryForStream(query, parameters, mapper)) {
            return stream.findAny();
        }
    }

    protected List<T> findMany(String query, SqlParameterSource parameters) {
        try (Stream<T> stream = jdbc.queryForStream(query, parameters, mapper)) {
            return stream.toList();
        }
    }

    protected long insert(String query, SqlParameterSource parameters) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(query, parameters, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new RepositoryException("Не удалось сохранить данные");
        }
        return id;
    }

    protected void update(String query, SqlParameterSource parameters) {
        int rowsUpdated = jdbc.update(query, parameters);
        if (rowsUpdated == 0) {
            throw new RepositoryException("Не удалось обновить данные");
        }
    }
}
