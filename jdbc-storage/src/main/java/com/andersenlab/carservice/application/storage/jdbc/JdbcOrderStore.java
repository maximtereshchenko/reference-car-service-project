package com.andersenlab.carservice.application.storage.jdbc;

import com.andersenlab.carservice.port.external.OrderStore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class JdbcOrderStore implements OrderStore {

    private final Database database;

    public JdbcOrderStore(Database database) {
        this.database = database;
    }

    @Override
    public void save(OrderEntity orderEntity) {
        if (findById(orderEntity.id()).isPresent()) {
            database.update(
                    """
                    update orders set
                    price = ?,
                    status = ?,
                    garage_slot_id = ?,
                    created = ?,
                    closed = ?
                    where id = ?;
                    """,
                    orderEntity.price(),
                    orderEntity.status(),
                    orderEntity.garageSlotId().orElse(null),
                    orderEntity.created(),
                    orderEntity.closed().orElse(null),
                    orderEntity.id()
            );
            database.update("delete from repairers_in_orders where order_id = ?;", orderEntity.id());
        } else {
            database.update(
                    """
                    insert into orders(id, price, status, garage_slot_id, created, closed)
                    values (?, ?, ?, ?, ?, ?);
                    """,
                    orderEntity.id(),
                    orderEntity.price(),
                    orderEntity.status(),
                    orderEntity.garageSlotId().orElse(null),
                    orderEntity.created(),
                    orderEntity.closed().orElse(null)
            );
        }
        for (var repairerId : orderEntity.repairers()) {
            database.update(
                    "insert into repairers_in_orders(repairer_id, order_id) values (?, ?);",
                    repairerId,
                    orderEntity.id()
            );
        }
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return database.execute("select * from orders where id = ?;", id)
                .map(this::orderEntity)
                .stream()
                .findAny();
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return database.execute(findAllSortedSql(sort))
                .map(this::orderProjection);
    }

    private OrderEntity orderEntity(ResultSet resultSet) throws SQLException {
        var id = UUID.fromString(resultSet.getString("id"));
        return new OrderEntity(
                id,
                resultSet.getLong("price"),
                OrderStatus.valueOf(resultSet.getString("status")),
                Optional.ofNullable(resultSet.getString("garage_slot_id"))
                        .map(UUID::fromString),
                database.execute("select repairer_id from repairers_in_orders where order_id = ?;", id)
                        .map(nestedResultSet -> nestedResultSet.getString("repairer_id"))
                        .stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet()),
                Instant.parse(resultSet.getString("created")),
                Optional.ofNullable(resultSet.getString("closed"))
                        .map(Instant::parse)
        );
    }

    private OrderProjection orderProjection(ResultSet resultSet) throws SQLException {
        return new OrderProjection(
                UUID.fromString(resultSet.getString("id")),
                resultSet.getLong("price"),
                OrderStatus.valueOf(resultSet.getString("status")),
                Instant.parse(resultSet.getString("created")),
                Optional.ofNullable(resultSet.getString("closed"))
                        .map(Instant::parse)
        );
    }

    private String findAllSortedSql(Sort sort) {
        return "select id, price, status, created, closed from orders order by " + sort.name().toLowerCase(Locale.ROOT);
    }
}
