CREATE TABLE repairers (
    id UUID PRIMARY KEY,
    name VARCHAR NOT NULL,
    status VARCHAR NOT NULL,
    is_deleted BOOLEAN NOT NULL
);