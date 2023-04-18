CREATE TABLE orders (
    id VARCHAR PRIMARY KEY,
    price BIGINT NOT NULL,
    status VARCHAR NOT NULL,
    garage_slot_id VARCHAR,
    created VARCHAR NOT NULL,
    closed VARCHAR
);