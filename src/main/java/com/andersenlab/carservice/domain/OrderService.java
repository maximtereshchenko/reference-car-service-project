package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.*;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotWasNotFound;
import com.andersenlab.carservice.port.usecase.exception.OrderWasNotFound;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

final class OrderService implements CreateOrderUseCase, ListOrdersUseCase, AssignGarageSlotToOrderUseCase, ViewOrderUseCase {

    private final OrderStore orderStore;
    private final GarageSlotStore garageSlotStore;
    private final Clock clock;

    OrderService(OrderStore orderStore, GarageSlotStore garageSlotStore, Clock clock) {
        this.orderStore = orderStore;
        this.garageSlotStore = garageSlotStore;
        this.clock = clock;
    }

    @Override
    public void create(UUID id, long price) {
        orderStore.save(new Order(id, price, clock.instant()).entity());
    }

    @Override
    public List<ListOrdersUseCase.OrderView> list(Sort sort) {
        return orderStore.findAllSorted(OrderStore.Sort.valueOf(sort.name()))
                .stream()
                .map(orderEntity ->
                        new ListOrdersUseCase.OrderView(
                                orderEntity.id(),
                                orderEntity.price(),
                                OrderStatus.valueOf(orderEntity.status().name()),
                                orderEntity.creation(),
                                orderEntity.closing()
                        )
                )
                .toList();
    }

    @Override
    public void assign(UUID orderId, UUID garageSlotId) {
        if (garageSlotStore.notExist(garageSlotId)) {
            throw new GarageSlotWasNotFound();
        }
        var order = orderStore.findById(orderId)
                .map(Order::new)
                .orElseThrow(OrderWasNotFound::new);

        orderStore.save(order.assignGarageSlot(garageSlotId).entity());
    }

    @Override
    public Optional<ViewOrderUseCase.OrderView> view(UUID id) {
        return orderStore.findById(id)
                .map(Order::new)
                .map(Order::view);
    }
}
