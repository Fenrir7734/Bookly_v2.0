DELETE FROM review;

INSERT INTO user (id, created_at, email, firstname, lastname, password, role, username)
VALUES (101, '2022-05-19 09:49:09.832115', 'adam.nowak@gmail.com', 'Adam', 'Nowak',
        '$2a$10$mQqFLlWERo/a51UoImP9ZuynHVas7WL.cDs0n18oUmIbfv27M4gJK', 'ROLE_USER', 'nowak');

INSERT INTO user (id, created_at, email, firstname, lastname, password, role, username)
VALUES (102, '2022-05-19 09:49:09.832115', 'jan.kowalski@gmail.com', 'Jan', 'Kowalski',
        '$2a$10$mQqFLlWERo/a51UoImP9ZuynHVas7WL.cDs0n18oUmIbfv27M4gJK', 'ROLE_USER', 'kowalski');


INSERT INTO book (id, author, cover, created_at, description, title, updated_at)
VALUES (101, 'George R.R. Martin',
        'https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1562726234i/13496.jpg',
        '2022-05-26 15:32:09.908486',
        'description',
        'A Game of Thrones', '2022-05-26 15:32:09.909206');

INSERT INTO book (id, author, cover, created_at, description, title, updated_at)
VALUES (102, 'James S.A. Corey',
        'https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1407572377i/12591698.jpg',
        '2022-05-26 15:32:09.959447',
        'description',
        'Calibans War', '2022-05-26 15:32:09.959472');

INSERT INTO review (book_id, user_id, content, created_at, rate, updated_at)
VALUES (101, 101,
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
        '2022-05-26 15:32:10.021749', 4, '2022-05-26 15:32:10.021772');


