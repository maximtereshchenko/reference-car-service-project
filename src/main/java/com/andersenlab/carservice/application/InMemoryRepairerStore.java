package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.RepairerStore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InMemoryRepairerStore implements RepairerStore {

    private final Map<UUID, RepairerEntity> map = new HashMap<>();

    @Override
    public void save(RepairerEntity repairerEntity) {
        map.put(repairerEntity.id(), repairerEntity);
    }

    @Override
    public Collection<RepairerEntity> findAll() {
        return map.values();
    }
}
