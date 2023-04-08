package com.andersenlab.carservice.port.usecase;

import java.util.List;
import java.util.UUID;

public interface ListGarageSlotsUseCase {

    List<GarageSlotView> list(Sort sort);

    enum Sort {
        ID
    }

    record GarageSlotView(UUID id) {}
}
