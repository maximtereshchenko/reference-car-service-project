package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface AssignGarageSlotToOrderUseCase {

    void assignGarageSlot(UUID orderId, UUID garageSlotId);
}
