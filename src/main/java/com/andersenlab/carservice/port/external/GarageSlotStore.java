package com.andersenlab.carservice.port.external;

import java.util.Collection;
import java.util.UUID;

public interface GarageSlotStore {

    void save(GarageSlotEntity repairerEntity);

    Collection<GarageSlotEntity> findAllSorted(Sort sort);

    void delete(UUID id);

    boolean hasNot(UUID id);

    GarageSlotEntity getById(UUID id);

    enum Sort {
        ID, STATUS
    }

    enum GarageSlotStatus {
        AVAILABLE, ASSIGNED
    }

    record GarageSlotEntity(UUID id, GarageSlotStatus status) {}
}
