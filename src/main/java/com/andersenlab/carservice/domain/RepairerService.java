package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservice.port.usecase.DeleteRepairerUseCase;
import com.andersenlab.carservice.port.usecase.ListRepairersUseCase;
import com.andersenlab.carservice.port.usecase.exception.RepairerIsAssigned;
import com.andersenlab.carservice.port.usecase.exception.RepairerWithSameIdExists;

import java.util.List;
import java.util.UUID;

final class RepairerService implements AddRepairerUseCase, ListRepairersUseCase, DeleteRepairerUseCase {

    private final RepairerStore repairerStore;

    RepairerService(RepairerStore repairerStore) {
        this.repairerStore = repairerStore;
    }

    @Override
    public void add(UUID id, String name) {
        if (repairerStore.has(id)) {
            throw new RepairerWithSameIdExists();
        }
        repairerStore.save(new RepairerStore.RepairerEntity(id, name, RepairerStore.RepairerStatus.AVAILABLE));
    }

    @Override
    public List<RepairerView> list(Sort sort) {
        return repairerStore.findAllSorted(RepairerStore.Sort.valueOf(sort.toString()))
                .stream()
                .map(this::repairerView)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (repairerStore.hasRepairerWithStatusAssigned(id)) {
            throw new RepairerIsAssigned();
        }
        repairerStore.delete(id);
    }

    boolean hasNotRepairer(UUID id) {
        return !repairerStore.has(id);
    }

    void markRepairerAsAssigned(UUID id) {
        var entity = repairerStore.getById(id);
        repairerStore.save(
                new RepairerStore.RepairerEntity(
                        entity.id(),
                        entity.name(),
                        RepairerStore.RepairerStatus.ASSIGNED
                )
        );
    }

    private RepairerView repairerView(RepairerStore.RepairerEntity repairerEntity) {
        return new RepairerView(
                repairerEntity.id(),
                repairerEntity.name(),
                RepairerStatus.valueOf(repairerEntity.status().name())
        );
    }
}
