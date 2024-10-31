# java-filmorate
В промежуточном задании была составлена схма будующей базы данных. Вот она, на рисунке ниже.
![Структура базы данных](/FilmorateDatabase.png)
## Некоторые запросы
А тут некоторые запросы на выборку данных, которые могут быть позже использованы в проекте:
- получаем список всех пользователей
```SQL
SELECT *
FROM users
```
- получаем имена и электронные адреса всех подтверждённых друзей пользователя (допустим, с user_id = 31)
```SQL
SELECT name, email
FROM users
WHERE user_id IN (
  SELECT friend_id
  FROM friends
  WHERE user_id = 31 AND status_id = 1)
```
- получаем имена и электронные адреса всех подтверждённых общих друзей двух пользователей (допустим, с user_id = 31 и user_id = 42)
```SQL
SELECT name, email
FROM users
WHERE user_id IN (
  (SELECT friend_id
  FROM friends
  WHERE user_id = 31 AND status_id = 1)
INTERSECT
  (SELECT friend_id
  FROM friends
  WHERE user_id = 42 AND status_id = 1)
)
``` 
- получаем список всех фильмов с их жанрами и рейтингами MBA
```SQL
SELECT f.name, f.description, f.release_date, f.duration, mba.mba_name, g.genre_name 
FROM films f
JOIN mba ON f.mba_id = mba.mba_id
JOIN film_genres fg ON fg.film_id = f.film_id 
JOIN genres g ON g.genre_id = fg.genre_id
```
- получаем список названий 10 лучших фильмов
```SQL
SELECT f.name, COUNT(l.*) AS rating
FROM films f
JOIN likes l ON l.film_id = f.film_id
GROUP BY f.film_id 
ORDER BY rating DESC
limit 10
```  
Конечно, это не все возможные запросы и их будет больше
