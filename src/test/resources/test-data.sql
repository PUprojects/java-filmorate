MERGE INTO mpa (mpa_id, mpa_name) VALUES (1, 'G');
MERGE INTO mpa (mpa_id, mpa_name) VALUES (2, 'PG');
MERGE INTO mpa (mpa_id, mpa_name) VALUES (3, 'PG-13');

MERGE INTO genres (genre_id, genre_name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, genre_name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, genre_name) VALUES (3, 'Мультфильм');

MERGE INTO users (user_id, email, login, user_name, birthday) VALUES
    (1, 'mymail@mail.ru', 'User1', 'Иван Иванов', '1999-03-12');
MERGE INTO users (user_id, email, login, user_name, birthday) VALUES
    (2, 'mymail@gmail.com', 'Use2', 'Юрий Петров', '1985-11-11');
MERGE INTO users (user_id, email, login, user_name, birthday) VALUES
    (3, 'mymail@yandex.ru', 'User3', 'Сергей Сергеев', '2002-10-08');

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 4;

MERGE INTO friends (user_id, friend_id) VALUES (1, 2);
MERGE INTO friends (user_id, friend_id) VALUES (1, 3);
MERGE INTO friends (user_id, friend_id) VALUES (2, 3);

MERGE INTO films (film_id, film_name, description, release_date, duration, mpa_id) VALUES
(1, 'Джентльмены',
    'Гангстеры всех мастей делят наркоферму. Закрученная экшен-комедия Гая Ричи с Мэттью Макконахи и Хью Грантом',
    '2019-05-05', 120, 3);
MERGE INTO films (film_id, film_name, description, release_date, duration, mpa_id) VALUES
    (2, 'Один дома',
    'Американское семейство отправляется из Чикаго в Европу, но в спешке сборов бестолковые родители забывают дома... одного из своих детей.',
    '1990-12-12', 103, 2);
MERGE INTO films (film_id, film_name, description, release_date, duration, mpa_id) VALUES
    (3, 'Шрэк',
    'Жил да был в сказочном государстве большой зеленый великан по имени Шрэк.',
    '2001-04-01', 107, 2);

    ALTER TABLE films ALTER COLUMN film_id RESTART WITH 4;

        MERGE INTO film_genres (genre_id, film_id) VALUES (1, 1);
        MERGE INTO film_genres (genre_id, film_id) VALUES (1, 2);
        MERGE INTO film_genres (genre_id, film_id) VALUES (1, 3);
        MERGE INTO film_genres (genre_id, film_id) VALUES (3, 3);

        MERGE INTO likes (film_id, user_id) VALUES (1, 1);
        MERGE INTO likes (film_id, user_id) VALUES (2, 1);
        MERGE INTO likes (film_id, user_id) VALUES (2, 2);
        MERGE INTO likes (film_id, user_id) VALUES (2, 3);
        MERGE INTO likes (film_id, user_id) VALUES (3, 2);
        MERGE INTO likes (film_id, user_id) VALUES (3, 3);
