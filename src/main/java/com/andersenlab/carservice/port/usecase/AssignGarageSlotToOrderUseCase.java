package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface AssignGarageSlotToOrderUseCase {

    void assign(UUID orderId, UUID garageSlotId);
}
