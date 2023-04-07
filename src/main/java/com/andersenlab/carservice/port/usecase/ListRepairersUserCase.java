package com.andersenlab.carservice.port.usecase;

import java.util.List;
import java.util.UUID;

public interface ListRepairersUserCase {

    List<RepairerView> list(Sort sort);

    enum Sort{
        NAME
    }

    record RepairerView(UUID id, String name) {}
}
