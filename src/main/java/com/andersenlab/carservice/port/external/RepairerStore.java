package com.andersenlab.carservice.port.external;

import java.util.Collection;
import java.util.UUID;

public interface RepairerStore {

    void save(RepairerEntity repairerEntity);

    Collection<RepairerEntity> findAllSorted(Sort sort);

    void delete(UUID id);

    boolean has(UUID repairerId);

    enum Sort {
        ID, NAME
    }

    record RepairerEntity(UUID id, String name) {}
}
