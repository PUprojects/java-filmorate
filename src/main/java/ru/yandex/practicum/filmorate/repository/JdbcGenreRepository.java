package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :id";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM genres WHERE genre_id IN (:ids)";

    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getAll() {
        return findMany(FIND_ALL_QUERY, new EmptySqlParameterSource());
    }

    @Override
    public List<Genre> getByIds(List<Long> ids) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("ids", ids);
        return findMany(FIND_BY_IDS_QUERY, source);
    }

    @Override
    public Optional<Genre> getById(long id) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", id);
        return findOne(FIND_BY_ID_QUERY, source);
    }
}
