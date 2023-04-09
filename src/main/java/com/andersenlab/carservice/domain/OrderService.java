package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.GarageSlotStore;
import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.*;
import com.andersenlab.carservice.port.usecase.exception.GarageSlotWasNotFound;
import com.andersenlab.carservice.port.usecase.exception.OrderWasNotFound;
import com.andersenlab.carservice.port.usecase.exception.RepairerWasNotFound;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

final class OrderService
        implements CreateOrderUseCase,
        ListOrdersUseCase,
        AssignGarageSlotToOrderUseCase,
        ViewOrderUseCase,
        AssignRepairerToOrderUseCase {

    private final OrderStore orderStore;
    private final GarageSlotStore garageSlotStore;
    private final RepairerStore repairerStore;
    private final Clock clock;

    OrderService(OrderStore orderStore, GarageSlotStore garageSlotStore, RepairerStore repairerStore, Clock clock) {
        this.orderStore = orderStore;
        this.garageSlotStore = garageSlotStore;
        this.repairerStore = repairerStore;
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
                                orderEntity.created(),
                                orderEntity.closed()
                        )
                )
                .toList();
    }

    @Override
    public void assignGarageSlot(UUID orderId, UUID garageSlotId) {
        if (garageSlotStore.hasNot(garageSlotId)) {
            throw new GarageSlotWasNotFound();
        }
        var order = orderStore.findById(orderId)
                .map(Order::new)
                .orElseThrow(OrderWasNotFound::new);

        orderStore.save(order.assignGarageSlot(garageSlotId).entity());
    }

    @Override
    public ViewOrderUseCase.OrderView view(UUID id) {
        return orderStore.findById(id)
                .map(Order::new)
                .map(Order::view)
                .orElseThrow(OrderWasNotFound::new);
    }

    @Override
    public void assignRepairer(UUID orderId, UUID repairerId) {
        if (repairerStore.hasNot(repairerId)) {
            throw new RepairerWasNotFound();
        }
        var order = orderStore.findById(orderId)
                .map(Order::new)
                .orElseThrow(OrderWasNotFound::new);

        orderStore.save(order.assignRepairer(repairerId).entity());
    }
}
