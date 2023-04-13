package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.port.external.OrderStore;

import java.util.*;

public final class InMemoryOrderStore implements OrderStore {

    private final Map<UUID, OrderEntity> map = new HashMap<>();
    private final Comparators comparators = Comparators.create();

    @Override
    public void save(OrderEntity orderEntity) {
        map.put(orderEntity.id(), orderEntity);
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return map.values()
                .stream()
                .sorted(comparators.comparator(sort))
                .map(orderEntity ->
                        new OrderProjection(
                                orderEntity.id(),
                                orderEntity.price(),
                                orderEntity.status(),
                                orderEntity.created(),
                                orderEntity.closed()
                        )
                )
                .toList();
    }
}
