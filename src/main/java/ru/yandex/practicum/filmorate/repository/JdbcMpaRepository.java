package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMpaRepository extends BaseRepository<Mpa> implements MpaRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = :id";

    public JdbcMpaRepository(NamedParameterJdbcOperations jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Mpa> getById(long id) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", id);
        return findOne(FIND_BY_ID_QUERY, source);
    }

    @Override
    public List<Mpa> getAll() {
        return findMany(FIND_ALL_QUERY, new EmptySqlParameterSource());
    }
}
