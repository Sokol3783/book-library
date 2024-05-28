DROP TABLE IF EXISTS registry;

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

INSERT INTO registry(book_id, reader_id)
VALUES (1, 1),
       (2, 1)