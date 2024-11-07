package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcMpaRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.repository.mappers")
@DisplayName("Тестирование Jdbc репозитория рейтингов MPA (JdbcMpaRepository)")
class JdbcMpaRepositoryTest {
    public static final long TEST_MPA_ID = 1;
    private final JdbcMpaRepository mpaRepository;

    private static Mpa getTestMpa() {
        Mpa mpa = new Mpa();
        mpa.setId(TEST_MPA_ID);
        mpa.setName("G");

        return mpa;
    }

    private static List<Mpa> getTestMpaAll() {
        ArrayList<Mpa> mpaAll = new ArrayList<>(3);

        mpaAll.add(getTestMpa());

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");
        mpaAll.add(mpa2);

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");
        mpaAll.add(mpa3);

        return mpaAll;
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию о рейтинге по заданному id")
    void shouldGetMpaRatingById() {
        Optional<Mpa> mpaOptional = mpaRepository.getById(TEST_MPA_ID);

        assertThat(mpaOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestMpa());
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию обо всех рейтингах MPA")
    void shouldGetAllMpaRatings() {
        List<Mpa> mpaList = mpaRepository.getAll();
        List<Mpa> testMpaList = getTestMpaAll();

        assertThat(mpaList).hasSize(testMpaList.size());

        for (int i = 0; i < mpaList.size(); ++i) {
            assertThat(mpaList.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(testMpaList.get(i));
        }
    }
}
