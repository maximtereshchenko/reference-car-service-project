package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;

import java.time.InstantSource;
import java.util.UUID;

final class CanceledOrder extends ObservedOrder {

    CanceledOrder(OrderStore.OrderEntity entity) {
        super(entity);
    }

    @Override
    public Order assignGarageSlot(UUID garageSlotId) {
        throw new RuntimeException(); //TODO
    }

    @Override
    public Order assignRepairer(UUID repairerId) {
        throw new RuntimeException(); //TODO
    }

    @Override
    public Order complete(InstantSource instantSource) {
        throw new RuntimeException(); //TODO
    }

    @Override
    public Order cancel(InstantSource instantSource) {
        throw new RuntimeException(); //TODO
    }
}
