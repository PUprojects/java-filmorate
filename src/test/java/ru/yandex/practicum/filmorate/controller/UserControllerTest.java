package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    private UserController userController;
    private Validator validator;

    @BeforeEach
    void initialize() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Должен создаватькорректно описанного пользователя")
    void shouldCreateUser() {
        User user = new User();

        user.setName("Иван иванов");
        user.setLogin("IvanovI");
        user.setBirthday(LocalDate.of(1983, 11, 11));
        user.setEmail("ivanov_i@yandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertTrue(violations.isEmpty(), "Не создан пользователь с корректно заданными параметрами");

        user.setId(1);

        violations = validator.validate(user, Marker.OnUpdate.class);
        assertTrue(violations.isEmpty(), "Не обновлён пользователь с корректно заданными параметрами");

        assertDoesNotThrow(() -> {
            userController.create(user);
        }, "Не создан пользователь с корректно заданными параметрами");
    }

    @Test
    @DisplayName("Должен возвращать список всех пользователей")
    void shouldReturnAllFilms() {
        User user1 = new User();

        user1.setName("Иван Иванов");
        user1.setLogin("IvanovI");
        user1.setBirthday(LocalDate.of(1983, 11, 11));
        user1.setEmail("ivanov_i@yandex.ru");

        User user2 = new User();

        user2.setName("Пётр Петров");
        user2.setLogin("PiterFirst");
        user2.setBirthday(LocalDate.of(1999, 12, 12));
        user2.setEmail("piter1@yandex.ru");

        User createdUser1 = userController.create(user1);
        User createdUser2 = userController.create(user2);

        List<User> allUsers = userController.getAll();

        assertEquals(2, allUsers.size(), "Количество созданных пользователей должно бытьравно 2");
        assertEquals(createdUser1, allUsers.get(0), "Первый пользователь не совпадат с возвращённым");
        assertEquals(createdUser2, allUsers.get(1), "Второй пользователь не совпадат с возвращённым");
    }

    @Test
    @DisplayName("Должен обновлять данные о пользователе")
    void shouldUpdateUserData() {
        User user = new User();

        user.setName("Иван иванов");
        user.setLogin("IvanovI");
        user.setBirthday(LocalDate.of(1983, 11, 11));
        user.setEmail("ivanov_i@yandex.ru");

        user = userController.create(user);

        user.setEmail("ivanov_work@yandex.ru");
        user.setBirthday(LocalDate.of(1983, 11, 1));

        User userFinal = user;
        assertDoesNotThrow(() -> {
            userController.update(userFinal);
        }, "Не обновлён пользователь с корректно заданными параметрами");
    }

    @Test
    @DisplayName("Дожен выдать ошибку, если не задан неверный id пользователя при обновлении")
    void shouldFailOnUpdateWithWrongId() {
        User user = new User();

        user.setId(88);
        user.setName("Иван иванов");
        user.setLogin("IvanovI");
        user.setBirthday(LocalDate.of(1983, 11, 11));
        user.setEmail("ivanov_i@yandex.ru");

        assertThrows(NotFoundException.class,
                () -> userController.update(user),
                "Был обновлён пользователь с несуществующим id");
    }

    @Test
    @DisplayName("Должен выдать ошибку при конструировании пользователя с некорректной почтой")
    void shouldFailOnCreteUserWithIncorrectEmail() {
        User user1 = new User();

        user1.setName("Иван Иванов");
        user1.setLogin("IvanovI");
        user1.setBirthday(LocalDate.of(1983, 11, 11));
        user1.setEmail("");

        User user2 = new User();

        user2.setName("Пётр Петров");
        user2.setLogin("PiterFirst");
        user2.setBirthday(LocalDate.of(1999, 12, 12));
        user2.setEmail("piter1");

        Set<ConstraintViolation<User>> violations = validator.validate(user1, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Создан пользователь с пустой почтой");

        user1.setId(1);

        violations = validator.validate(user1, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Обновлён пользователь с пустой почтой");

        violations = validator.validate(user2, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Создан пользователь с некорректной почтой");

        user2.setId(1);

        violations = validator.validate(user2, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Обновлён пользователь с некорректной почтой");
    }

    @Test
    @DisplayName("Должен создавать пользователя с пустым именем и записывать туда логин")
    void shouldCreateUserWithEmptyName() {
        User user = new User();

        user.setName("");
        user.setLogin("IvanovI");
        user.setBirthday(LocalDate.of(1983, 11, 11));
        user.setEmail("ivanov_i@yandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertTrue(violations.isEmpty(), "Не создан пользователь с пустым именем");

        user = userController.create(user);
        assertEquals(user.getName(), user.getLogin(), "Имя и логин не совпадают после создания");

        user.setName("");
        violations = validator.validate(user, Marker.OnUpdate.class);
        assertTrue(violations.isEmpty(), "Не обновлён пользователь с пустым именем");

        user = userController.update(user);
        assertEquals(user.getName(), user.getLogin(), "Имя и логин не совпадают после обновления");
    }

    @Test
    @DisplayName("Не должен создавать пользователя с пустым логином")
    void shouldFailOnCreateUserWithEmptyLogin() {
        User user = new User();

        user.setName("Иван иванов");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1983, 11, 11));
        user.setEmail("ivanov_i@yandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Создан пользователь с пустым логином");

        violations = validator.validate(user, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Обновлён пользователь с пустым логином");
    }

    @Test
    @DisplayName("Не должен создавать пользователя с датой рождения в будующем")
    void shouldFailOnCreateUserWithFutureBirthdate() {
        User user = new User();

        user.setName("Иван иванов");
        user.setLogin("IvanovI");
        user.setBirthday(LocalDate.of(2983, 11, 11));
        user.setEmail("ivanov_i@yandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);
        assertFalse(violations.isEmpty(), "Создан пользователь с датой рождения в будущем");

        violations = validator.validate(user, Marker.OnUpdate.class);
        assertFalse(violations.isEmpty(), "Обновлён пользователь с датой рождения в будущем");
    }
}
