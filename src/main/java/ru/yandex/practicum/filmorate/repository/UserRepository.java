package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    Optional<User> getById(long userId);

    List<User> getByIds(List<Long> ids);

    User create(User user);

    void update(User user);

    void addFiend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
