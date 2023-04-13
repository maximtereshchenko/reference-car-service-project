package com.andersenlab.carservice.port.usecase;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ViewOrderUseCase {

    OrderView view(UUID id);

    record OrderView(
            UUID id,
            long price,
            OrderStatus status,
            Optional<UUID> garageSlotId,
            Set<UUID> repairers,
            Instant created,
            Optional<Instant> closed
    ) {}
}
