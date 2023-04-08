package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;
import com.andersenlab.carservice.port.usecase.CreateOrderUseCase;
import com.andersenlab.carservice.port.usecase.ListOrdersUseCase;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

final class OrderService implements CreateOrderUseCase, ListOrdersUseCase {

    private final OrderStore orderStore;
    private final Clock clock;

    OrderService(OrderStore orderStore, Clock clock) {
        this.orderStore = orderStore;
        this.clock = clock;
    }

    @Override
    public void create(UUID id, long price) {
        orderStore.save(
                new OrderStore.OrderEntity(
                        id,
                        price,
                        OrderStore.OrderStatus.IN_PROCESS,
                        clock.instant(),
                        Optional.empty()
                )
        );
    }

    @Override
    public List<OrderView> list(Sort sort) {
        return orderStore.findAllSorted(OrderStore.Sort.valueOf(sort.name()))
                .stream()
                .map(orderEntity ->
                        new OrderView(
                                orderEntity.id(),
                                orderEntity.price(),
                                OrderStatus.valueOf(orderEntity.status().name()),
                                orderEntity.creation(),
                                orderEntity.closing()
                        )
                )
                .toList();
    }
}
