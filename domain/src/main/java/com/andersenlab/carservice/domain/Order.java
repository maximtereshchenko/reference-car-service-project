package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.ViewOrderUseCase;

import java.time.InstantSource;
import java.util.Optional;
import java.util.UUID;

interface Order {

    ViewOrderUseCase.OrderView view();

    OrderStore.OrderEntity entity();

    Order assignGarageSlot(UUID garageSlotId);

    Order assignRepairer(UUID repairerId);

    Order complete(InstantSource instantSource);

    Order cancel(InstantSource instantSource);

    Optional<UUID> garageSlot();

    Iterable<UUID> repairers();
}
