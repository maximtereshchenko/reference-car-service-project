package com.andersenlab.carservice.application.storage.inmemory;

import com.andersenlab.Comparators;
import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InMemoryGarageSlotStore implements GarageSlotStore {

    private final Map<UUID, GarageSlotEntity> map = new HashMap<>();
    private final Comparators comparators = Comparators.create();

    @Override
    public void save(GarageSlotEntity garageSlotEntity) {
        map.put(garageSlotEntity.id(), garageSlotEntity);
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return map.values().stream()
                .sorted(comparators.comparator(sort))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        map.remove(id);
    }

    @Override
    public boolean has(UUID id) {
        return map.containsKey(id);
    }

    @Override
    public GarageSlotEntity getById(UUID id) {
        return map.get(id);
    }

    @Override
    public boolean hasGarageSlotWithStatusAssigned(UUID id) {
        if (!has(id)) {
            return false;
        }
        return map.get(id).status() == GarageSlotStatus.ASSIGNED;
    }
}
