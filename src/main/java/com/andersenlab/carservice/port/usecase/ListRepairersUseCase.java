package com.andersenlab.carservice.port.usecase;

import java.util.List;
import java.util.UUID;

public interface ListRepairersUseCase {

    List<RepairerView> list(Sort sort);

    enum Sort {
        ID, NAME, STATUS
    }

    enum RepairerStatus {
        AVAILABLE, ASSIGNED
    }

    record RepairerView(UUID id, String name, RepairerStatus status) {}
}
