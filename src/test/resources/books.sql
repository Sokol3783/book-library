ALTER TABLE registry
    DROP CONSTRAINT fk_book_id;

DROP TABLE IF exists book;

CREATE TABLE IF NOT EXISTS book
(
    id     BIGSERIAL PRIMARY KEY,
    title  VARCHAR(100),
    author VARCHAR(30)
);

INSERT INTO book(title, author)
VALUES ('The Dark Tower', 'Steven King'),
       ('The name of the Wind', 'Patric Rotfuss'),
       ('A Game of Thrones', 'George Martin');

ALTER TABLE registry
    ADD CONSTRAINT fk_book_id
        FOREIGN KEY (book_id)
            REFERENCES book (id);