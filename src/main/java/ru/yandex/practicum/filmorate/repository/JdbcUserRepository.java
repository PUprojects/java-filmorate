package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :user_id";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, user_name, birthday) VALUES " +
            "(:email, :login, :name, :birthday)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = :email, login = :login, user_name = :name, " +
            "birthday = :birthday WHERE user_id = :id";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES " +
            "(:user_id, :friend_id)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends " +
            "WHERE user_id = :user_id AND friend_id = :friend_id";
    private static final String FIND_FRIENDS = "SELECT * FROM users WHERE user_id IN" +
            "(SELECT friend_id FROM friends WHERE user_id = :user_id)";
    private static final String FIND_COMMON_FRIENDS = "SELECT u.* FROM users u " +
            "JOIN friends f1 ON u.user_id = f1.friend_id " +
            "JOIN friends f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = :user_id AND f2.user_id = :other_id";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM users WHERE user_id IN (:ids)";


    public JdbcUserRepository(NamedParameterJdbcOperations jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> getAll() {
        return findMany(FIND_ALL_QUERY, new EmptySqlParameterSource());
    }

    @Override
    public Optional<User> getById(long userId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("user_id", userId);
        return findOne(FIND_BY_ID_QUERY, source);
    }

    @Override
    public List<User> getByIds(List<Long> ids) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("ids", ids);
        return findMany(FIND_BY_IDS_QUERY, source);
    }

    @Override
    public User create(User user) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("email", user.getEmail());
        source.addValue("login", user.getLogin());
        source.addValue("name", user.getName());
        source.addValue("birthday", user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        user.setId(insert(INSERT_QUERY, source));
        return user;
    }

    @Override
    public void update(User user) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("id", user.getId());
        source.addValue("email", user.getEmail());
        source.addValue("login", user.getLogin());
        source.addValue("name", user.getName());
        source.addValue("birthday", user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        update(UPDATE_QUERY, source);
    }

    @Override
    public void addFiend(long userId, long friendId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("user_id", userId);
        source.addValue("friend_id", friendId);

        jdbc.update(ADD_FRIEND_QUERY, source);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("user_id", userId);
        source.addValue("friend_id", friendId);

        jdbc.update(DELETE_FRIEND_QUERY, source);
    }

    @Override
    public List<User> getFriends(long userId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("user_id", userId);

        return findMany(FIND_FRIENDS, source);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("user_id", userId);
        source.addValue("other_id", otherId);

        return findMany(FIND_COMMON_FRIENDS, source);
    }
}
