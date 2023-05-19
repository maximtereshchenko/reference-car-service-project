CREATE TABLE repairers_in_orders (
    repairer_id UUID,
    order_id UUID,
    PRIMARY KEY (repairer_id, order_id),
    FOREIGN KEY (repairer_id) REFERENCES repairers(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);