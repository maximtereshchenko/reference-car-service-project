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
        AssignRepairerToOrderUseCase,
        CompleteOrderUseCase,
        CancelOrderUseCase {

    private final OrderStore orderStore;
    private final GarageSlotStore garageSlotStore;
    private final RepairerStore repairerStore;
    private final Clock clock;
    private final OrderFactory orderFactory;

    OrderService(
            OrderStore orderStore,
            GarageSlotStore garageSlotStore,
            RepairerStore repairerStore,
            Clock clock,
            OrderFactory orderFactory
    ) {
        this.orderStore = orderStore;
        this.garageSlotStore = garageSlotStore;
        this.repairerStore = repairerStore;
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    @Override
    public void create(UUID id, long price) {
        orderStore.save(orderFactory.createNew(id, price, clock.instant()).entity());
    }

    @Override
    public List<ListOrdersUseCase.OrderView> list(Sort sort) {
        return orderStore.findAllSorted(OrderStore.Sort.valueOf(sort.name()))
                .stream()
                .map(this::orderView)
                .toList();
    }

    @Override
    public void assignGarageSlot(UUID orderId, UUID garageSlotId) {
        if (garageSlotStore.hasNot(garageSlotId)) {
            throw new GarageSlotWasNotFound();
        }
        orderStore.save(order(orderId).assignGarageSlot(garageSlotId).entity());
    }

    @Override
    public ViewOrderUseCase.OrderView view(UUID id) {
        return order(id).view();
    }

    @Override
    public void assignRepairer(UUID orderId, UUID repairerId) {
        if (repairerStore.hasNot(repairerId)) {
            throw new RepairerWasNotFound();
        }
        orderStore.save(order(orderId).assignRepairer(repairerId).entity());
    }

    @Override
    public void complete(UUID id) {
        orderStore.save(
                order(id)
                        .complete(clock)
                        .entity()
        );
    }

    @Override
    public void cancel(UUID id) {
        orderStore.save(
                order(id)
                        .cancel(clock)
                        .entity()
        );
    }

    private ListOrdersUseCase.OrderView orderView(OrderStore.OrderProjection orderEntity) {
        return new ListOrdersUseCase.OrderView(
                orderEntity.id(),
                orderEntity.price(),
                OrderStatus.valueOf(orderEntity.status().name()),
                orderEntity.created(),
                orderEntity.closed()
        );
    }

    private Order order(UUID id) {
        return orderStore.findById(id)
                .map(orderFactory::order)
                .orElseThrow(OrderWasNotFound::new);
    }
}
