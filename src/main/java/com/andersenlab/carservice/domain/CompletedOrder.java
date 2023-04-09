package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.exception.OrderHasBeenAlreadyCompleted;

import java.time.InstantSource;
import java.util.UUID;

final class CompletedOrder extends ObservedOrder {

    CompletedOrder(OrderStore.OrderEntity entity) {
        super(entity);
    }

    @Override
    public Order assignGarageSlot(UUID garageSlotId) {
        throw new OrderHasBeenAlreadyCompleted();
    }

    @Override
    public Order assignRepairer(UUID repairerId) {
        throw new OrderHasBeenAlreadyCompleted();
    }

    @Override
    public Order complete(InstantSource instantSource) {
        throw new OrderHasBeenAlreadyCompleted();
    }

    @Override
    public Order cancel(InstantSource instantSource) {
        throw new OrderHasBeenAlreadyCompleted();
    }
}
