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
        repairerStore.save(new Repairer(id, name).entity());
    }

    @Override
    public List<RepairerView> list(Sort sort) {
        return repairerStore.findAllSorted(RepairerStore.Sort.valueOf(sort.toString()))
                .stream()
                .map(Repairer::new)
                .map(Repairer::view)
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
        repairerStore.save(
                new Repairer(repairerStore.getById(id))
                        .asAssigned()
                        .entity()
        );
    }
}
