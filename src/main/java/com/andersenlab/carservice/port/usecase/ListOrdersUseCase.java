package com.andersenlab.carservice.port.usecase;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListOrdersUseCase {

    List<OrderView> list(Sort sort);

    enum Sort {
        ID, PRICE, STATUS, CREATION_TIMESTAMP, CLOSING_TIMESTAMP
    }

    enum OrderStatus {
        IN_PROCESS, COMPLETED, CANCELED
    }

    record OrderView(UUID id, long price, OrderStatus status, Instant creation, Optional<Instant> closing) {}
}
