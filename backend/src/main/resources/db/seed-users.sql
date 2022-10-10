-- liquibase formatted sql

-- changeset Karol Hetman:SEED_USERS
INSERT INTO user (id, created_at, email, firstname, lastname, password, role, username)
VALUES (1, '2022-05-19 09:23:47.296698', 'jan.kowalski123@gmail.com', 'Jan', 'Kowalski',
        '$2a$10$gWVVXkBXT9wRQYwijDs3h.JOL3a2pFQWKFphsJqr8OU/46VHzweua', 'ROLE_ADMIN', 'kowalski123'),
       (2, '2022-05-19 09:49:09.832115', 'adam.nowak132@gmail.com', 'Adam', 'Nowak',
        '$2a$10$mQqFLlWERo/a51UoImP9ZuynHVas7WL.cDs0n18oUmIbfv27M4gJK', 'ROLE_USER', 'nowak123'),
       (3, '2022-05-19 12:37:28.781658', 'karol.hetman@gmail.com', 'Karol', 'Hetman',
        '$2a$10$xEAy59EEb3zQI.L0GXQSz.qXDijuLIgSIx5XfFvyiSJ.Fw0jWG3HS', 'ROLE_USER', 'hetman123'),
       (4, '2022-05-19 12:37:57.762463', 'michal.grzeszuk123@gmail.com', 'Michał', 'Grzeszuk',
        '$2a$10$2x8.I6cVM9MSOU8OdZJYr.k82jL5xIFK7QMeBrkTnCfwkMf0twexm', 'ROLE_USER', 'grzeszuk123'),
       (5, '2022-05-19 12:37:57.762463', 'michal.goluch@gmail.com', 'Michał', 'Goluch',
        '$2a$10$2x8.I6cVM9MSOU8OdZJYr.k82jL5xIFK7QMeBrkTnCfwkMf0twexm', 'ROLE_USER', 'goluch123');
