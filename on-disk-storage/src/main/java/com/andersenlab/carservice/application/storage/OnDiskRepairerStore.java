package com.andersenlab.carservice.application.storage;

import com.andersenlab.Comparators;
import com.andersenlab.carservice.port.external.RepairerStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class OnDiskRepairerStore implements RepairerStore {

    private final StateFile stateFile;
    private final Comparators comparators = Comparators.create();

    public OnDiskRepairerStore(StateFile stateFile) {
        this.stateFile = stateFile;
    }

    @Override
    public void save(RepairerEntity repairerEntity) {
        if (has(repairerEntity.id())) {
            delete(repairerEntity.id());
        }
        var state = stateFile.read();
        var copy = new ArrayList<>(state.repairers());
        copy.add(repairerEntity);
        stateFile.write(state.withRepairers(copy));
    }

    @Override
    public Collection<RepairerEntity> findAllSorted(Sort sort) {
        return stateFile.read()
                .repairers()
                .stream()
                .sorted(comparators.comparator(sort))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        var state = stateFile.read();
        var updated = state.repairers()
                .stream()
                .filter(repairerEntity -> !repairerEntity.id().equals(id))
                .toList();
        stateFile.write(state.withRepairers(updated));
    }

    @Override
    public boolean has(UUID id) {
        return findAllSorted(RepairerStore.Sort.ID)
                .stream()
                .map(RepairerEntity::id)
                .anyMatch(id::equals);
    }

    @Override
    public boolean hasRepairerWithStatusAssigned(UUID id) {
        var repairerEntity = getById(id);
        if (repairerEntity == null) {
            return false;
        }
        return repairerEntity.status() == RepairerStatus.ASSIGNED;
    }

    @Override
    public RepairerEntity getById(UUID id) {
        return findAllSorted(Sort.ID)
                .stream()
                .filter(repairerEntity -> repairerEntity.id().equals(id))
                .findAny()
                .orElse(null);
    }
}
