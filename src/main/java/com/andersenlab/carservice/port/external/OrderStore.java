package com.andersenlab.carservice.port.external;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderStore {

    void save(OrderEntity orderEntity);

    Collection<OrderEntity> findAllSorted(Sort sort);

    enum Sort {
        ID, PRICE, STATUS, CREATION_TIMESTAMP, CLOSING_TIMESTAMP
    }

    enum OrderStatus {
        IN_PROCESS, COMPLETED, CANCELED
    }

    record OrderEntity(UUID id, long price, OrderStatus status, Instant creation, Optional<Instant> closing) {}
}
