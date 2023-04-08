package com.andersenlab.carservice.port.usecase;

import java.util.List;
import java.util.UUID;

public interface ListRepairersUseCase {

    List<RepairerView> list(Sort sort);

    enum Sort{
        ID, NAME
    }

    record RepairerView(UUID id, String name) {}
}
