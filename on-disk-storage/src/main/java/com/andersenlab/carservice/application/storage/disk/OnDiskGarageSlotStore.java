package com.andersenlab.carservice.application.storage.disk;

import com.andersenlab.Comparators;
import com.andersenlab.carservice.port.external.GarageSlotStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class OnDiskGarageSlotStore implements GarageSlotStore {

    private final StateFile stateFile;
    private final Comparators comparators = Comparators.create();

    public OnDiskGarageSlotStore(StateFile stateFile) {
        this.stateFile = stateFile;
    }

    @Override
    public void save(GarageSlotEntity garageSlotEntity) {
        if (has(garageSlotEntity.id())) {
            delete(garageSlotEntity.id());
        }
        var state = stateFile.read();
        var copy = new ArrayList<>(state.garageSlots());
        copy.add(garageSlotEntity);
        stateFile.write(state.withGarageSlots(copy));
    }

    @Override
    public Collection<GarageSlotEntity> findAllSorted(Sort sort) {
        return stateFile.read()
                .garageSlots()
                .stream()
                .sorted(comparators.comparator(sort))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        var state = stateFile.read();
        var updated = state.garageSlots()
                .stream()
                .filter(garageSlotEntity -> !garageSlotEntity.id().equals(id))
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
