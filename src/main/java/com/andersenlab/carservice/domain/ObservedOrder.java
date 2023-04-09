package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.OrderStatus;
import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

abstract class ObservedOrder implements Order {

    private final OrderStore.OrderEntity entity;

    ObservedOrder(OrderStore.OrderEntity entity) {
        this.entity = entity;
    }

    @Override
    public ViewOrderUseCase.OrderView view() {
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

    @Override
    public OrderStore.OrderEntity entity() {
        return entity;
    }

    @Override
    public Optional<UUID> garageSlot() {
        return entity.garageSlotId();
    }

    @Override
    public Iterable<UUID> repairers() {
        return entity.repairers();
    }
}
