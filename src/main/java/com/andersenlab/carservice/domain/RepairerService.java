package com.andersenlab.carservice.domain;

import com.andersenlab.carservice.port.external.RepairerStore;
import com.andersenlab.carservice.port.usecase.AddRepairerUseCase;
import com.andersenlab.carservice.port.usecase.ListRepairersUserCase;

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
    public Iterable<RepairerView> list() {
        return repairerStore.findAll()
                .stream()
                .map(repairerEntity -> new RepairerView(repairerEntity.id(), repairerEntity.name()))
                .toList();
    }
}
