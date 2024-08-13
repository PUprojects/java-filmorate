package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void initialize() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Должен создавать корректно описанный фильм")
    void shouldCreateFilm() {
        Film film = new Film();

        film.setName("Титаник");
        film.setDescription("Про корабль и айсберг");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1997, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
        assertTrue(violations.isEmpty(), "Не создан фильм с корректно заданными параметрами");

        film.setId(1);

        violations = validator.validate(film, Marker.OnUpdate.class);
        assertTrue(violations.isEmpty(), "Не обновлён фильм с корректно заданными параметрами");

        assertDoesNotThrow(() -> {
            filmController.create(film);
        }, "Не создан фильм с корректно заданными параметрами");
    }

    @Test
    @DisplayName("Должен возвращать список всех фильмов")
    void shouldReturnAllFilms() {
        Film film1 = new Film();

        film1.setName("Титаник");
        film1.setDescription("Про корабль и айсберг");
        film1.setDuration(180);
        film1.setReleaseDate(LocalDate.of(1997, 1, 1));

        Film film2 = new Film();

        film2.setName("Властелин колец");
        film2.setDescription("Про хоббитов и путешествия");
        film2.setDuration(558);
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));

        Film createdFilm1 = filmController.create(film1);
        Film createdFilm2 = filmController.create(film2);

        List<Film> allFilms = filmController.getAll();

        assertEquals(2, allFilms.size(), "Количество созданных фильмов должно бытьравно 2");
        assertEquals(createdFilm1, allFilms.get(0), "Первый фильм не совпадат с возвращённым");
        assertEquals(createdFilm2, allFilms.get(1), "Второй фильм не совпадат с возвращённым");
    }

    @Test
    @DisplayName("Должен обновлять данные о фильме")
    void shouldUpdateFilm() {
        Film film = new Film();

        film.setName("Титаник");
        film.setDescription("Про корабль и айсберг");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1997, 1, 1));

        film = filmController.create(film);

        film.setDescription("Про корабль и айсберг (режиссёрская версия)");
        film.setDuration(270);

        Film finalFilm = film;
        assertDoesNotThrow(() -> {
            filmController.update(finalFilm);
        }, "Не обновлён фильм с корректно заданными параметрами");
    }

    @Test
    @DisplayName("Дожен выдать ошибку, если не задан неверный id фильма при обновлении")
    void shouldFailOnUpdateWithWrongId() {
        Film film = new Film();

        film.setId(9);
        film.setName("Титаник");
        film.setDescription("Про корабль и айсберг");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1997, 1, 1));

        assertThrows(NotFoundException.class,
                () -> filmController.update(film),
                "Был обновлён фильм с несуществующим id");
    }

    @Test
    @DisplayName("Должен выдать ошибку при конструировании фильма с пустым именем")
    void shouldFailOnCreteWithEmptyName() {
        Film film = new Film();

        film.setName("");
        film.setDescription("Про корабль и айсберг");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1997, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при создании");

        violations = validator.validate(film, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при обновлении");
    }

    @Test
    @DisplayName("Должен выдать ошибку при конструировании фильма с длинным описанием")
    void shouldFailOnCreteWithLongDescription() {
        Film film = new Film();

        film.setName("Однажды");
        film.setDescription("""
                Однажды, в студеную зимнюю пору,
                Я из лесу вышел; был сильный мороз.
                Гляжу, поднимается медленно в гору
                Лошадка, везущая хворосту воз.
                И, шествуя важно, в спокойствии чинном,
                Лошадку ведет под уздцы мужичок
                В больших сапогах, в полушубке овчинном,
                В больших рукавицах… а сам с ноготок!
                — Здорово, парнище!— «Ступай себе мимо!»
                — Уж больно ты грозен, как я погляжу!
                Откуда дровишки?— «Из лесу, вестимо;
                Отец, слышишь, рубит, а я отвожу».
                (В лесу раздавался топор дровосека.)
                — А что, у отца-то большая семья?
                «Семья-то большая, да два человека
                Всего мужиков-то: отец мой да я…»""");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1900, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при создании");

        violations = validator.validate(film, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при обновлении");
    }

    @Test
    @DisplayName("Должен выдать ошибку при конструировании фильма с некорректной датой выхода")
    void shouldFailOnCreateWithIncorrectReleaseDate() {
        Film film = new Film();

        film.setId(9);
        film.setName("Бородино");
        film.setDescription("Скажи-ка, дядя");
        film.setDuration(180);
        film.setReleaseDate(LocalDate.of(1812, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при создании");

        violations = validator.validate(film, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при обновлении");
    }

    @Test
    @DisplayName("Должен выдать ошибку при конструировании фильма с нулевой или отрицательной дительностью")
    void shouldFailOnCreateWithZeroOrNegativeDuration() {
        Film film = new Film();

        film.setId(9);
        film.setName("Титаник");
        film.setDescription("Про корабль и айсберг");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(1997, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при создании");

        violations = validator.validate(film, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при обновлении");

        film.setDuration(-10);
        violations = validator.validate(film, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при создании");

        violations = validator.validate(film, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Должна быть сформирована ошибка валидации при обновлении");
    }
}
