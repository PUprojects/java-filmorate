package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JdbcUserRepository userRepository;

    private void setNameSameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User get(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
    }

    public User create(User user) {
        setNameSameAsLogin(user);

        return userRepository.create(user);
    }

    public User update(User user) {
        get(user.getId());
        setNameSameAsLogin(user);
        userRepository.update(user);

        return user;
    }

    private void isUsersInTable(List<Long> usersIds) {
        List<User> foundUsers = userRepository.getByIds(usersIds);
        if (foundUsers.size() != usersIds.size()) {
            usersIds.removeAll(foundUsers.stream().map(User::getId).toList());
            throw new NotFoundException("User(s) not found with id(s) = " + usersIds.toString());
        }
    }

    public void addFriend(long userId, long friendId) {
        isUsersInTable(new ArrayList<>(List.of(userId, friendId)));
        userRepository.addFiend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        isUsersInTable(new ArrayList<>(List.of(userId, friendId)));
        userRepository.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        get(userId);
        return userRepository.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        isUsersInTable(new ArrayList<>(List.of(userId, otherId)));
        return userRepository.getCommonFriends(userId, otherId);
    }
}
