CREATE TABLE repairers (
    id VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    status VARCHAR NOT NULL,
    is_deleted BOOLEAN NOT NULL
);