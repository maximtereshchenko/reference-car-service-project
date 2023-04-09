package com.andersenlab.carservice.port.external;

import java.util.Collection;
import java.util.UUID;

public interface RepairerStore {

    void save(RepairerEntity repairerEntity);

    Collection<RepairerEntity> findAllSorted(Sort sort);

    void delete(UUID id);

    boolean has(UUID id);

    boolean hasRepairerWithStatusAssigned(UUID id);

    RepairerEntity getById(UUID id);

    enum Sort {
        ID, NAME, STATUS
    }

    enum RepairerStatus {
        AVAILABLE, ASSIGNED
    }

    record RepairerEntity(UUID id, String name, RepairerStatus status) {}
}
