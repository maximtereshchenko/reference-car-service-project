package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.exception.OrderHasBeenAlreadyCanceled;

import java.time.InstantSource;
import java.util.UUID;

final class CanceledOrder extends ObservedOrder {

    CanceledOrder(OrderStore.OrderEntity entity) {
        super(entity);
    }

    @Override
    public Order assignGarageSlot(UUID garageSlotId) {
        throw new OrderHasBeenAlreadyCanceled();
    }

    @Override
    public Order assignRepairer(UUID repairerId) {
        throw new OrderHasBeenAlreadyCanceled();
    }

    @Override
    public Order complete(InstantSource instantSource) {
        throw new OrderHasBeenAlreadyCanceled();
    }

    @Override
    public Order cancel(InstantSource instantSource) {
        throw new OrderHasBeenAlreadyCanceled();
    }
}
