package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.util.*;

public final class InMemoryGarageSlotStore implements GarageSlotStore {

    private final Map<UUID, GarageSlotEntity> map = new HashMap<>();
    private final Map<Sort, Comparator<GarageSlotEntity>> comparators = Map.of(
            Sort.ID, Comparator.comparing(GarageSlotEntity::id),
            Sort.STATUS, Comparator.comparing(GarageSlotEntity::status)
    );

    @Override
    public void save(GarageSlotEntity garageSlotEntity) {
        map.put(garageSlotEntity.id(), garageSlotEntity);
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return map.values().stream()
                .sorted(comparators.get(sort))
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
