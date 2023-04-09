package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.OrderStore;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class OrderFactory {

    Order createNew(UUID id, long price, Instant timestamp) {
        return new ProcessingOrder(
                new OrderStore.OrderEntity(
                        id,
                        price,
                        OrderStore.OrderStatus.IN_PROCESS,
                        Optional.empty(),
                        Set.of(),
                        timestamp,
                        Optional.empty()
                )
        );
    }

    Order order(OrderStore.OrderEntity entity) {
        if (entity.status() == OrderStore.OrderStatus.COMPLETED) {
            return new CompletedOrder(entity);
        }
        if (entity.status() == OrderStore.OrderStatus.CANCELED) {
            return new CanceledOrder(entity);
        }
        return new ProcessingOrder(entity);
    }
}
