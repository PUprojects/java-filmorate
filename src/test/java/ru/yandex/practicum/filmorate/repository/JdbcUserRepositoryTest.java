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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcUserRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.repository.mappers")
@DisplayName("Тестирование Jdbc репозитория пользователей (JdbcUserRepository)")
class JdbcUserRepositoryTest {
    public static final long TEST_USER_ID = 1;
    private final JdbcUserRepository userRepository;

    private static User getTestUser() {
        User user = new User();

        user.setId(TEST_USER_ID);
        user.setEmail("mymail@mail.ru");
        user.setLogin("User1");
        user.setName("Иван Иванов");
        user.setBirthday(LocalDate.of(1999, 3, 12));

        return user;
    }

    private static User getTestUserToUpdate() {
        User user = new User();

        user.setId(TEST_USER_ID);
        user.setEmail("mymail@gov.us");
        user.setLogin("User5");
        user.setName("Анна Сидорова");
        user.setBirthday(LocalDate.of(2005, 11, 21));

        return user;
    }

    private User getTestUserToCreate() {
        User user = new User();

        user.setId(0);
        user.setEmail("mymail@vk.com");
        user.setLogin("User4");
        user.setName("Виталий Максимов");
        user.setBirthday(LocalDate.of(1965, 3, 17));

        return user;
    }

    List<User> getAllTestUsers() {
        ArrayList<User> users = new ArrayList<>(3);

        users.add(getTestUser());

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("mymail@gmail.com");
        user2.setLogin("Use2");
        user2.setName("Юрий Петров");
        user2.setBirthday(LocalDate.of(1985, 11, 11));
        users.add(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setEmail("mymail@yandex.ru");
        user3.setLogin("User3");
        user3.setName("Сергей Сергеев");
        user3.setBirthday(LocalDate.of(2002, 10, 8));
        users.add(user3);

        return users;
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию обо всех пользователях")
    void getAll() {
        List<User> usersFromDB = userRepository.getAll();
        List<User> usersTest = getAllTestUsers();

        assertThat(usersFromDB).hasSize(usersTest.size());

        for (int i = 0; i < usersFromDB.size(); ++i) {
            assertThat(usersFromDB.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(usersTest.get(i));
        }
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию о пользователе по заданному id")
    void shouldGetUserById() {
        Optional<User> optionalUser = userRepository.getById(TEST_USER_ID);

        assertThat(optionalUser)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("Репозиторий должен прочитать из БД информацию о нескольких пользователях по id")
    void shouldGetUsersByIds() {
        List<User> usersFromDB = userRepository.getByIds(List.of(1L, 2L));
        List<User> usersTest = getAllTestUsers();

        assertThat(usersFromDB).hasSize(2);

        assertThat(usersFromDB.get(0))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(0));

        assertThat(usersFromDB.get(1))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(1));
    }

    @Test
    @DisplayName("Репозиторий должен создавать запиь о пользователе")
    void shouldCreateNewUserRecord() {
        User userToCreate = getTestUserToCreate();
        User createdUser = userRepository.create(userToCreate);
        Optional<User> userFromDB = userRepository.getById(createdUser.getId());

        assertThat(userFromDB)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(createdUser);

        assertThat(createdUser)
                .usingRecursiveComparison()
                .ignoringFields("user_id")
                .isEqualTo(userToCreate);
    }

    @Test
    @DisplayName("Репозиторий должен обновлять запиь о пользователе")
    void shouldUpdateUserRecord() {
        User userBeforeUpdate = userRepository.getById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + TEST_USER_ID));

        assertThat(userBeforeUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());

        userRepository.update(getTestUserToUpdate());

        User userAfterUpdate = userRepository.getById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + TEST_USER_ID));

        assertThat(userAfterUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestUserToUpdate());
    }

    @Test
    @DisplayName("Репозиторий должен возвращать друзей пользователя")
    void shouldGetUsersFriends() {
        List<User> friendsList = userRepository.getFriends(TEST_USER_ID);
        List<User> usersTest = getAllTestUsers();

        assertThat(friendsList).hasSize(2);

        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(1));

        assertThat(friendsList.get(1))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(2));
    }

    @Test
    @DisplayName("Репозиторий должен добавлять друга для пользователя")
    void shouldAddFiendForUser() {
        List<User> usersTest = getAllTestUsers();
        userRepository.addFiend(3, TEST_USER_ID);

        List<User> friendsList = userRepository.getFriends(3);

        assertThat(friendsList).hasSize(1);

        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(0));
    }

    @Test
    @DisplayName("Репозиторий должен удалять запись о дружбе")
    void shouldDeleteFriend() {
        List<User> friendsList = userRepository.getFriends(TEST_USER_ID);

        assertThat(friendsList).hasSize(2);

        userRepository.deleteFriend(TEST_USER_ID, 2);

        friendsList = userRepository.getFriends(TEST_USER_ID);
        assertThat(friendsList).hasSize(1);

        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(getAllTestUsers().get(2));
    }

    @Test
    @DisplayName("Репозиторий должен возвращать список общих друзей двух пользователей")
    void shouldGetCommonFriendsOfTwoUsers() {
        List<User> friendsList = userRepository.getCommonFriends(TEST_USER_ID, 2);

        assertThat(friendsList).hasSize(1);
        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(getAllTestUsers().get(2));
    }
}
