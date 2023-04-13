package com.andersenlab.carservice.port.usecase;

import java.util.List;
import java.util.UUID;

public interface ListGarageSlotsUseCase {

    List<GarageSlotView> list(Sort sort);

    enum Sort {
        ID, STATUS
    }

    enum GarageSlotStatus {
        AVAILABLE, ASSIGNED
    }

    record GarageSlotView(UUID id, GarageSlotStatus status) {}
}
