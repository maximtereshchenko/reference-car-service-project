package com.andersenlab.carservce.port.usecase;

import java.util.UUID;

public interface ListRepairersUserCase {

    Iterable<RepairerView> list();

    record RepairerView(UUID id, String name) {}
}
