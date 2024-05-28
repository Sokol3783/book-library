ALTER TABLE registry
    DROP CONSTRAINT fk_reader_id;

DROP TABLE IF EXISTS reader;

CREATE TABLE IF NOT EXISTS reader
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(30)
);

INSERT INTO reader(name)
VALUES ('Mike Douglas'),
       ('Fedor Trybeckoi'),
       ('IVAN MAZEPA');

ALTER TABLE registry
    ADD CONSTRAINT fk_reader_id
        FOREIGN KEY (reader_id)
            REFERENCES reader (id);
