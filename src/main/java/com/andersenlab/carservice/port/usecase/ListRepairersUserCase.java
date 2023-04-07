package com.andersenlab.carservice.port.usecase;

import java.util.List;
import java.util.UUID;

public interface ListRepairersUserCase {

    List<RepairerView> list();

    record RepairerView(UUID id, String name) {}
}
