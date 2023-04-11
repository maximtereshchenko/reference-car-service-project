package com.andersenlab.carservice.application.storage.disk;

import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.util.*;

public final class OnDiskGarageSlotStore implements GarageSlotStore {

    private final StateFile stateFile;
    private final Map<Sort, Comparator<GarageSlotEntity>> comparators = Map.of(
            Sort.ID, Comparator.comparing(GarageSlotEntity::id),
            Sort.STATUS, Comparator.comparing(GarageSlotEntity::status)
    );

    public OnDiskGarageSlotStore(StateFile stateFile) {
        this.stateFile = stateFile;
    }

    @Override
    public void save(GarageSlotEntity repairerEntity) {
        if (has(repairerEntity.id())) {
            delete(repairerEntity.id());
        }
        var state = stateFile.read();
        var copy = new ArrayList<>(state.garageSlots());
        copy.add(repairerEntity);
        stateFile.write(state.withGarageSlots(copy));
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return stateFile.read()
                .garageSlots()
                .stream()
                .sorted(comparators.get(sort))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        var state = stateFile.read();
        var updated = state.garageSlots()
                .stream()
                .filter(garageSlotEntity -> garageSlotEntity.id() != id)
                .toList();
        stateFile.write(state.withGarageSlots(updated));
    }

    @Override
    public boolean has(UUID id) {
        return findAllSorted(Sort.ID)
                .stream()
                .map(GarageSlotEntity::id)
                .anyMatch(id::equals);
    }

    @Override
    public GarageSlotEntity getById(UUID id) {
        return findAllSorted(Sort.ID)
                .stream()
                .filter(garageSlotEntity -> garageSlotEntity.id().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean hasGarageSlotWithStatusAssigned(UUID id) {
        var garageSlotEntity = getById(id);
        if (garageSlotEntity == null) {
            return false;
        }
        return garageSlotEntity.status() == GarageSlotStatus.ASSIGNED;
    }
}
