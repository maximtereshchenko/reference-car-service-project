package com.andersenlab.carservice.port.usecase;

import java.util.UUID;

public interface AssignRepairerToOrderUseCase {

    void assignRepairer(UUID orderId, UUID repairerId);
}
