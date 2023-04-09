package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.exception.OrderHasNoGarageSlotAssigned;
import com.andersenlab.carservice.port.usecase.exception.OrderHasNoRepairersAssigned;

import java.time.Instant;
import java.time.InstantSource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class ProcessingOrder extends ObservedOrder {

    ProcessingOrder(OrderStore.OrderEntity entity) {
        super(entity);
    }

    @Override
    public Order assignGarageSlot(UUID garageSlotId) {
        return new ProcessingOrder(
                new OrderStore.OrderEntity(
                        entity().id(),
                        entity().price(),
                        entity().status(),
                        Optional.of(garageSlotId),
                        entity().repairers(),
                        entity().created(),
                        entity().closed()
                )
        );
    }

    @Override
    public Order assignRepairer(UUID repairerId) {
        var copy = new HashSet<>(entity().repairers());
        copy.add(repairerId);
        return new ProcessingOrder(
                new OrderStore.OrderEntity(
                        entity().id(),
                        entity().price(),
                        entity().status(),
                        entity().garageSlotId(),
                        Set.copyOf(copy),
                        entity().created(),
                        entity().closed()
                )
        );
    }

    @Override
    public Order complete(InstantSource instantSource) {
        if (entity().garageSlotId().isEmpty()) {
            throw new OrderHasNoGarageSlotAssigned();
        }
        if (entity().repairers().isEmpty()) {
            throw new OrderHasNoRepairersAssigned();
        }
        Instant closed = instantSource.instant();
        return new CompletedOrder(
                new OrderStore.OrderEntity(
                        entity().id(),
                        entity().price(),
                        OrderStore.OrderStatus.COMPLETED,
                        entity().garageSlotId(),
                        entity().repairers(),
                        entity().created(),
                        Optional.of(closed)
                )
        );
    }

    @Override
    public Order cancel(InstantSource instantSource) {
        Instant closed = instantSource.instant();
        return new CanceledOrder(
                new OrderStore.OrderEntity(
                        entity().id(),
                        entity().price(),
                        OrderStore.OrderStatus.CANCELED,
                        entity().garageSlotId(),
                        entity().repairers(),
                        entity().created(),
                        Optional.of(closed)
                )
        );
    }
}
