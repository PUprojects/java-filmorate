CREATE TABLE IF NOT EXISTS users
(
    user_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL UNIQUE,
    login       VARCHAR(255) NOT NULL UNIQUE,
    user_name   VARCHAR(255) NOT NULL,
    birthday    DATE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id     BIGINT,
    friend_id   BIGINT,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    film_name       VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    release_date    DATE NOT NULL,
    duration        INT NOT NULL,
    mpa_id          BIGINT
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    genre_name  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres
(
    genre_id    BIGINT,
    film_id     BIGINT,
    PRIMARY KEY (genre_id, film_id)
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    mpa_name    VARCHAR(10)

);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INT,
    user_id INT,
    PRIMARY KEY (film_id, user_id)
);
