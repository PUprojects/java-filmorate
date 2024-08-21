package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    private void setNameSameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User get(long userId) {
        return userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
    }

    public User create(User user) {
        setNameSameAsLogin(user);

        return userStorage.create(user);
    }

    public User update(User user) {
        get(user.getId());
        setNameSameAsLogin(user);
        userStorage.update(user);

        return user;
    }

    public void addFriend(long userId, long friendId) {
        get(userId);
        get(friendId);

        userStorage.addFiend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        get(userId);
        get(friendId);

        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        get(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        get(userId);
        get(otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }
}
