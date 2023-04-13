package com.andersenlab.carservice.application.storage;

import com.andersenlab.carservice.port.external.OrderStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public final class OnDiskOrderStore implements OrderStore {

    private final StateFile stateFile;
    private final Comparators comparators = Comparators.create();

    public OnDiskOrderStore(StateFile stateFile) {
        this.stateFile = stateFile;
    }

    @Override
    public void save(OrderEntity orderEntity) {
        var state = stateFile.read();
        var copy = new ArrayList<>(state.orders());
        copy.removeIf(stored -> stored.id().equals(orderEntity.id()));
        copy.add(orderEntity);
        stateFile.write(state.withOrders(copy));
    }

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        return stateFile.read()
                .orders()
                .stream()
                .filter(orderEntity -> orderEntity.id().equals(id))
                .findAny();
    }

    @Override
    public Collection<OrderProjection> findAllSorted(Sort sort) {
        return stateFile.read()
                .orders()
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
