package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import org.apache.commons.collections4.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {
    private static final String GET_FILM_GENRES_QUERY = "SELECT g.genre_id, g.genre_name FROM genres g " +
            "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = :film_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.mpa_name, COUNT(l.*) AS likes_count " +
            "FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "WHERE f.film_id = :film_id " +
            "GROUP BY f.film_id, m.mpa_name";
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.mpa_name, COUNT(l.*) AS likes_count " +
            "FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes l ON l.film_id = f.film_id " +
            "GROUP BY f.film_id, m.mpa_name";
    private static final String INSERT_QUERY = "INSERT INTO films (film_name, description, release_date, " +
            "duration, mpa_id) VALUES (:name, :desc, :release, :duration, :mpa)";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = :film_id";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO film_genres (genre_id, film_id) VALUES " +
            "(:genre_id, :film_id)";
    private static final String UPDATE_QUERY = "UPDATE films SET film_name = :name, description = :desc, " +
            "release_date = :release, duration = :duration, mpa_id = :mpa WHERE film_id = :id";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES " +
            "(:film_id, :user_id)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id";

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    private LinkedHashSet<Genre> getFilmGenres(long filmId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("film_id", filmId);
        List<Genre> genres;
        try (Stream<Genre> stream = jdbc.queryForStream(GET_FILM_GENRES_QUERY, source, new GenreRowMapper())) {
            genres = stream.toList();
        }
        return new LinkedHashSet<>(genres);
    }

    private void setFilmGenres(Film film) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("film_id", film.getId());
        jdbc.update(DELETE_GENRES_QUERY, source);
        MapSqlParameterSource[] params = film.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("genre_id", genre.getId())
                        .addValue("film_id", film.getId()))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(INSERT_GENRES_QUERY, params);
    }

    @Override
    public List<Film> getAll() {
        return getPopular(0);
    }

    @Override
    public Optional<Film> getById(long filmId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("film_id", filmId);
        Optional<Film> result = findOne(FIND_BY_ID_QUERY, source);
        result.ifPresent(film -> film.setGenres(getFilmGenres(filmId)));
        return result;
    }

    @Override
    public Film create(Film film) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("name", film.getName());
        source.addValue("desc", film.getDescription());
        source.addValue("release", film.getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        source.addValue("duration", film.getDuration());
        source.addValue("mpa", film.getMpa().getId());

        film.setId(insert(INSERT_QUERY, source));
        if(CollectionUtils.isNotEmpty(film.getGenres()))
            setFilmGenres(film);

        return film;
    }

    @Override
    public void update(Film film) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", film.getId());
        source.addValue("name", film.getName());
        source.addValue("desc", film.getDescription());
        source.addValue("release", film.getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        source.addValue("duration", film.getDuration());
        source.addValue("mpa", film.getMpa().getId());

        update(UPDATE_QUERY, source);
        setFilmGenres(film);
    }

    @Override
    public void addLike(long filmId, long userId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("film_id", filmId);
        source.addValue("user_id", userId);

        jdbc.update(ADD_LIKE_QUERY, source);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("film_id", filmId);
        source.addValue("user_id", userId);

        jdbc.update(DELETE_LIKE_QUERY, source);
    }

    @Override
    public List<Film> getPopular(int count) {
        String query = (count > 0) ?
                (FIND_ALL_QUERY + " ORDER BY likes_count DESC LIMIT " + count) : FIND_ALL_QUERY;

        List<Film> films = findMany(query, new EmptySqlParameterSource());

        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
        }
        return films;
    }
}
