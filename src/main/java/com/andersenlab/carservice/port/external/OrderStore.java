package com.andersenlab.carservice.port.external;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderStore {

    void save(OrderEntity orderEntity);

    Optional<OrderEntity> findById(UUID id);

    Collection<OrderProjection> findAllSorted(Sort sort);

    enum Sort {
        ID, PRICE, STATUS, CREATION_TIMESTAMP, CLOSING_TIMESTAMP
    }

    enum OrderStatus {
        IN_PROCESS, COMPLETED, CANCELED
    }

    record OrderEntity(
            UUID id,
            long price,
            OrderStatus status,
            Optional<UUID> garageSlotId,
            Instant creation,
            Optional<Instant> closing
    ) {}

    record OrderProjection(UUID id, long price, OrderStatus status, Instant creation, Optional<Instant> closing) {}
}
