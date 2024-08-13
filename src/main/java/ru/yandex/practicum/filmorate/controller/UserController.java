package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        log.info("Get all users start");
        List<User> result = userService.getAll();
        log.info("Get all users end");
        return result;
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable long userId) {
        log.info("Get user {} start", userId);
        return userService.get(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable long userId) {
        log.info("Get friends for user {} start", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        log.info("Get common friends for user {} and other user {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.OnCreate.class})
    public User create(@RequestBody @Valid User newUser) {
        log.info("Post new user {} start", newUser);
        userService.create(newUser);
        log.info("Post new user {} end", newUser);
        return newUser;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User update(@RequestBody @Valid User user) {
        log.info("Update user {} start", user);
        userService.update(user);
        log.info("Update user {} end", user);
        return user;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Add friend {} to user {} start", friendId, userId);
        userService.addFriend(userId, friendId);
        log.info("Add friend {} to user {} end", friendId, userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Delete friend {} to user {} start", friendId, userId);
        userService.deleteFriend(userId, friendId);
        log.info("Delete friend {} to user {} end", friendId, userId);
    }
}
