package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long newFilmId = 0;
    private final HashMap<Long, Film> films = new HashMap<>();
    private final HashMap<Long, Set<Long>> likes = new HashMap<>();

    private long getNewFilmId() {
        return ++newFilmId;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> get(long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Film create(Film film) {
        film.setId(getNewFilmId());
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public int addLike(long filmId, long userId) {
        Set<Long> filmLikes = likes.computeIfAbsent(filmId, id -> new HashSet<>());
        filmLikes.add(userId);

        return filmLikes.size();
    }

    @Override
    public int deleteLike(long filmId, long userId) {
        Set<Long> filmLikes = likes.computeIfAbsent(filmId, id -> new HashSet<>());
        filmLikes.remove(userId);

        return filmLikes.size();
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .toList();
    }
}
