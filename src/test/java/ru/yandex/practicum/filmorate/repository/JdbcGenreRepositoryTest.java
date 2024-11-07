package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcGenreRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.repository.mappers")
@DisplayName("Тестирование Jdbc репозитория жанров фильмов (JdbcGenreRepository)")
class JdbcGenreRepositoryTest {
    public static final long TEST_GENRE_ID = 1;
    private final JdbcGenreRepository genreRepository;

    static private Genre getTestGenre() {
        Genre genre = new Genre();

        genre.setId(TEST_GENRE_ID);
        genre.setName("Комедия");

        return genre;
    }

    static private List<Genre> getAllTestGenres() {
        ArrayList<Genre> genres = new ArrayList<>(3);

        genres.add(getTestGenre());

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");
        genres.add(genre2);

        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");
        genres.add(genre3);

        return genres;
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию обо всех жанрах")
    void shouldGetAllGenres() {
        List<Genre> genresFromDB = genreRepository.getAll();
        List<Genre> genresTest = getAllTestGenres();

        assertThat(genresFromDB).hasSize(genresTest.size());

        for (int i = 0; i < genresFromDB.size(); ++i) {
            assertThat(genresFromDB.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(genresTest.get(i));
        }
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию о нескольких жанрах по id")
    void shouldGetGenresByIds() {
        List<Genre> genresFromDB = genreRepository.getByIds(List.of(1L, 3L));
        List<Genre> genresTest = getAllTestGenres();

        assertThat(genresFromDB).hasSize(2);

        assertThat(genresFromDB.get(0))
                .usingRecursiveComparison()
                .isEqualTo(genresTest.get(0));

        assertThat(genresFromDB.get(1))
                .usingRecursiveComparison()
                .isEqualTo(genresTest.get(2));
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию о жанре по заданному id")
    void shouldGetGenreById() {
        Optional<Genre> genreOptional = genreRepository.getById(TEST_GENRE_ID);

        assertThat(genreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestGenre());
    }
}
