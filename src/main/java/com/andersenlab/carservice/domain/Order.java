package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.OrderStatus;
import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class Order {

    private final OrderStore.OrderEntity entity;

    Order(UUID id, long price, Instant timestamp) {
        this(
                new OrderStore.OrderEntity(
                        id,
                        price,
                        OrderStore.OrderStatus.IN_PROCESS,
                        Optional.empty(),
                        Set.of(),
                        timestamp,
                        Optional.empty()
                )
        );
    }

    Order(OrderStore.OrderEntity entity) {
        this.entity = entity;
    }

    ViewOrderUseCase.OrderView view() {
        return new ViewOrderUseCase.OrderView(
                entity.id(),
                entity.price(),
                OrderStatus.valueOf(entity.status().name()),
                entity.garageSlotId(),
                Set.copyOf(entity.repairers()),
                entity.created(),
                entity.closed()
        );
    }

    OrderStore.OrderEntity entity() {
        return entity;
    }

    Order assignGarageSlot(UUID garageSlotId) {
        return new Order(
                new OrderStore.OrderEntity(
                        entity.id(),
                        entity.price(),
                        entity.status(),
                        Optional.of(garageSlotId),
                        entity.repairers(),
                        entity.created(),
                        entity.closed()
                )
        );
    }

    Order assignRepairer(UUID repairerId) {
        var copy = new HashSet<>(entity.repairers());
        copy.add(repairerId);
        return new Order(
                new OrderStore.OrderEntity(
                        entity.id(),
                        entity.price(),
                        entity.status(),
                        entity.garageSlotId(),
                        Set.copyOf(copy),
                        entity.created(),
                        entity.closed()
                )
        );
    }

    Order complete(Clock clock) {
        return new Order(
                new OrderStore.OrderEntity(
                        entity.id(),
                        entity.price(),
                        OrderStore.OrderStatus.COMPLETED,
                        entity.garageSlotId(),
                        entity.repairers(),
                        entity.created(),
                        Optional.of(clock.instant())
                )
        );
    }
}
