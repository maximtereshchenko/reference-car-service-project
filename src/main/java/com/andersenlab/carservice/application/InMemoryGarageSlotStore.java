package com.andersenlab.carservice.application;

import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.util.*;

public final class InMemoryGarageSlotStore implements GarageSlotStore {

    private final Map<UUID, GarageSlotEntity> map = new HashMap<>();
    private final Map<Sort, Comparator<GarageSlotEntity>> comparators = Map.of(
            Sort.ID, Comparator.comparing(GarageSlotEntity::id)
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
}
