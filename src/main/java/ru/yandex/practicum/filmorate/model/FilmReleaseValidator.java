package ru.yandex.practicum.filmorate.model;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895,12,28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !localDate.isBefore(FIRST_FILM_RELEASE);
    }
}
