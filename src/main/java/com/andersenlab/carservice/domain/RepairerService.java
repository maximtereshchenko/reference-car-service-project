package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservice.port.usecase.ListRepairersUserCase;

import java.util.List;
import java.util.UUID;

final class RepairerService implements AddRepairerUseCase, ListRepairersUserCase {

    private final RepairerStore repairerStore;

    RepairerService(RepairerStore repairerStore) {
        this.repairerStore = repairerStore;
    }

    @Override
    public void add(UUID id, String name) {
        repairerStore.save(new RepairerStore.RepairerEntity(id, name));
    }

    @Override
    public List<RepairerView> list(Sort sort) {
        return repairerStore.findAllSorted(RepairerStore.Sort.valueOf(sort.toString()))
                .stream()
                .map(repairerEntity -> new RepairerView(repairerEntity.id(), repairerEntity.name()))
                .toList();
    }
}
