package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long newUserId = 0;
    private final HashMap<Long, User> users = new HashMap<>();
    private final HashMap<Long, Set<Long>> userFiendsIds = new HashMap<>();


    private long getNewUserId() {
        return ++newUserId;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        user.setId(getNewUserId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void addFiend(long userId, long friendId) {
        userFiendsIds.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);
        userFiendsIds.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userFiendsIds.computeIfAbsent(userId, id -> new HashSet<>()).remove(friendId);
        userFiendsIds.computeIfAbsent(friendId, id -> new HashSet<>()).remove(userId);
    }

    @Override
    public List<User> getFriends(long userId) {
        Set<Long> friendsIds = userFiendsIds.computeIfAbsent(userId, id -> new HashSet<>());

        return friendsIds.stream()
                .map(users::get)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        Set<Long> friendsIds = userFiendsIds.computeIfAbsent(userId, id -> new HashSet<>());
        Set<Long> otherFriendsIds = userFiendsIds.computeIfAbsent(otherId, id -> new HashSet<>());

        return friendsIds.stream()
                .filter(otherFriendsIds::contains)
                .map(users::get)
                .toList();
    }
}
