package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Long, User> users = new HashMap<>();
    private long newUserId = 0;

    private long getNewUserId() {
        return ++newUserId;
    }

    private void setNameSameAsLogin(User user) {
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Get all users start");
        List<User> result = new ArrayList<>(users.values());
        log.info("Get all users end");
        return result;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User create(@RequestBody @Valid User newUser) {
        log.info("Post new user {} start", newUser);
        newUser.setId(getNewUserId());
        setNameSameAsLogin(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Post new user {} end", newUser);
        return newUser;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User update(@RequestBody @Valid User user) {
        log.info("Update user {} start", user);
        User savedUser = users.get(user.getId());
        if(savedUser == null) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        setNameSameAsLogin(user);
        users.put(user.getId(), user);

        log.info("Update user {} end", user);
        return user;
    }
}
