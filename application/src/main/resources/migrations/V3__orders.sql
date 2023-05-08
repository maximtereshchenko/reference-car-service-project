CREATE TABLE orders (
    id UUID PRIMARY KEY,
    price BIGINT NOT NULL,
    status VARCHAR NOT NULL,
    garage_slot_id UUID,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    closed TIMESTAMP WITH TIME ZONE
);