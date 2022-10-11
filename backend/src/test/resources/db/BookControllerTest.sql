DELETE
FROM book;

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