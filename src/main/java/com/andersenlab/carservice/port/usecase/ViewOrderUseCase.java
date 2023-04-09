package com.andersenlab.carservice.port.usecase;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ViewOrderUseCase {

    OrderView view(UUID id);

    record OrderView(
            UUID id,
            long price,
            OrderStatus status,
            Optional<UUID> garageSlotId,
            Instant created,
            Optional<Instant> closed
    ) {}
}
