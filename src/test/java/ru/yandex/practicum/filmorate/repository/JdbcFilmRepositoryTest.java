package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcFilmRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.repository.mappers")
@DisplayName("Тестирование Jdbc репозитория фильмов (JdbcFilmRepository)")
class JdbcFilmRepositoryTest {
    public static final long TEST_FILM_ID = 1;
    private final JdbcFilmRepository jdbcFilmRepository;

    private static Film getTestFilm() {
        Film film = new Film();

        film.setId(TEST_FILM_ID);
        film.setName("Джентльмены");
        film.setDescription("Гангстеры всех мастей делят наркоферму. Закрученная экшен-комедия Гая Ричи с Мэттью Макконахи и Хью Грантом");
        film.setReleaseDate(LocalDate.of(2019, 5, 5));
        film.setDuration(120);

        film.setMpa(new Mpa());
        film.getMpa().setId(3);
        film.getMpa().setName("PG-13");

        film.setGenres(new LinkedHashSet<>());
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        film.getGenres().add(genre);

        film.setLikesCount(1);

        return film;
    }

    private static Film getTestFilmToCreateOrUpdate() {
        Film film = new Film();

        film.setId(TEST_FILM_ID);
        film.setName("Остров проклятых");
        film.setDescription("Два американских судебных пристава отправляются на один из островов в штате Массачусетс");
        film.setReleaseDate(LocalDate.of(2010, 2, 13));
        film.setDuration(138);

        film.setMpa(new Mpa());
        film.getMpa().setId(3);
        film.getMpa().setName("PG-13");

        film.setGenres(new LinkedHashSet<>());
        Genre genre = new Genre();
        genre.setId(2);
        genre.setName("Драма");
        film.getGenres().add(genre);

        film.setLikesCount(1);

        return film;
    }

    private static List<Film> getAllTestFilms() {
        ArrayList<Film> films = new ArrayList<>();

        films.add(getTestFilm());

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");

        Film film = new Film();
        film.setId(2);
        film.setName("Один дома");
        film.setDescription("Американское семейство отправляется из Чикаго в Европу, но в спешке сборов бестолковые родители забывают дома... одного из своих детей.");
        film.setReleaseDate(LocalDate.of(1990, 12, 12));
        film.setDuration(103);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        film.getMpa().setName("PG");
        film.setGenres(new LinkedHashSet<>());
        film.getGenres().add(genre1);
        film.setLikesCount(3);
        films.add(film);

        film = new Film();
        film.setId(3);
        film.setName("Шрэк");
        film.setDescription("Жил да был в сказочном государстве большой зеленый великан по имени Шрэк.");
        film.setReleaseDate(LocalDate.of(2001, 4, 1));
        film.setDuration(107);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        film.getMpa().setName("PG");
        film.setGenres(new LinkedHashSet<>());
        film.getGenres().add(genre1);
        film.getGenres().add(genre3);
        film.setLikesCount(2);
        films.add(film);

        return films;
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию обо всех фильмах")
    void shouldGetAllFilms() {
        List<Film> filmsFromDB = jdbcFilmRepository.getAll();
        List<Film> filmsTest = getAllTestFilms();

        assertThat(filmsFromDB).hasSize(filmsTest.size());

        for (int i = 0; i < filmsFromDB.size(); ++i) {
            assertThat(filmsFromDB.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(filmsTest.get(i));
        }
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию о фильме по заданному id")
    void shouldGetFilmById() {
        Optional<Film> optionalFilm = jdbcFilmRepository.getById(TEST_FILM_ID);

        assertThat(optionalFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("Репозиторий должен создавать запиь о фильме")
    void shouldCreateFimRecord() {
        Film filmToCreate = getTestFilmToCreateOrUpdate();
        Film createdFilm = jdbcFilmRepository.create(filmToCreate);
        Optional<Film> userFromDB = jdbcFilmRepository.getById(createdFilm.getId());

        assertThat(userFromDB)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(createdFilm);

        filmToCreate.setLikesCount(0);
        assertThat(createdFilm)
                .usingRecursiveComparison()
                .ignoringFields("user_id")
                .isEqualTo(filmToCreate);

    }

    @Test
    @DisplayName("Репозиторий должен обновлять запиь о фильме")
    void shouldUpdateFilmRecord() {
        Film filmBeforeUpdate = jdbcFilmRepository.getById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + TEST_FILM_ID));

        assertThat(filmBeforeUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());

        jdbcFilmRepository.update(getTestFilmToCreateOrUpdate());

        Film userAfterUpdate = jdbcFilmRepository.getById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + TEST_FILM_ID));

        assertThat(userAfterUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestFilmToCreateOrUpdate());
    }

    @Test
    @DisplayName("Репозиторий должен добавлять записи о лайках")
    void shouldAddLikeRecord() {
        Film film = jdbcFilmRepository.getById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(1);

        jdbcFilmRepository.addLike(TEST_FILM_ID, 2);

        film = jdbcFilmRepository.getById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Репозиторий должен удалять записи о лайках")
    void shouldDeleteLikeRecord() {
        Film film = jdbcFilmRepository.getById(2)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(3);

        jdbcFilmRepository.deleteLike(2, 1);

        film = jdbcFilmRepository.getById(2)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Репозиторий должен возвращать сортированный список популярных фильмов")
    void shouldGetPopularFilms() {
        List<Film> popularFilms = jdbcFilmRepository.getPopular(2);
        List<Film> filmsTest = getAllTestFilms();

        assertThat(popularFilms).hasSize(2);

        assertThat(popularFilms.get(0))
                .usingRecursiveComparison()
                .isEqualTo(filmsTest.get(1));
        assertThat(popularFilms.get(1))
                .usingRecursiveComparison()
                .isEqualTo(filmsTest.get(2));
    }
}
