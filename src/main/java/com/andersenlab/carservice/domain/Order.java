package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.OrderStatus;
import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.time.Instant;
import java.util.Optional;
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
                        entity.created(),
                        entity.closed()
                )
        );
    }
}
