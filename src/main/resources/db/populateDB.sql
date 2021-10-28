DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);


insert into meals (user_id, date_time, description, calories)
values (100000, '2020-01-30 10:0', 'Завтрак', 500),
       (100000, '2020-01-30 13:0', 'Обед', 1000),
       (100000, '2020-01-30 20:0', 'Ужин', 500),
       (100000, '2020-01-31 0:0', 'Еда на граничное значение', 100),
       (100000, '2020-01-31 10:0', 'Завтрак', 500),
       (100000, '2020-01-31 13:0', 'Обед', 1000),
       (100000, '2020-01-31 20:0', 'Ужин', 410),
       (100001, '2020-01-31 10:0', 'Завтрак', 510),
       (100001, '2020-01-31 20:0', 'Ужин', 1510);