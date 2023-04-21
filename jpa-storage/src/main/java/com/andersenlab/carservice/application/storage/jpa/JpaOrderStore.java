package com.andersenlab.carservice.application.storage.jpa;

import com.andersenlab.carservice.port.external.OrderStore;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class JpaOrderStore implements OrderStore {

    private final Database database;

    public JpaOrderStore(Database database) {
        this.database = database;
    }

    @Override
    public void save(OrderEntity orderEntity) {
        database.findById(orderEntity.id(), OrderJpaEntity.class)
                .ifPresentOrElse(
                        orderJpaEntity -> orderJpaEntity.update(orderEntity),
                        () -> database.persist(new OrderJpaEntity(orderEntity))
                );
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return database.findById(id, OrderJpaEntity.class)
                .map(OrderJpaEntity::orderEntity);
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return database.query(findAllQuery(sort), OrderJpaEntity.class)
                .stream()
                .map(OrderJpaEntity::orderProjection)
                .toList();
    }

    private String findAllQuery(Sort sort) {
        return "select o from OrderJpaEntity o order by o." + sort.name().toLowerCase(Locale.ROOT);
    }
}
