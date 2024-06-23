DROP TABLE IF EXISTS book CASCADE;
DROP TABLE IF EXISTS reader CASCADE;
DROP TABLE IF EXISTS registry CASCADE;

CREATE TABLE IF NOT EXISTS book
(
    id     BIGSERIAL PRIMARY KEY,
    title  VARCHAR(100),
    author VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS reader
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS registry
(
    id        BIGSERIAL PRIMARY KEY,
    book_id   BIGINT UNIQUE,
    reader_id BIGINT
);

ALTER TABLE registry
    ADD CONSTRAINT fk_book_id
        FOREIGN KEY (book_id)
            REFERENCES book (id);

ALTER TABLE registry
    ADD CONSTRAINT fk_reader_id
        FOREIGN KEY (reader_id)
            REFERENCES reader (id);